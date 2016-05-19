package com.puff.plugin.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import com.puff.core.Puff;
import com.puff.framework.utils.JsonUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;

public class QuartzManager {

	private static final Log log = LogFactory.get();
	private Scheduler scheduler;

	private QuartzManager() {
		QuartzPlugin plugin = Puff.getPlugin(QuartzPlugin.class);
		if (plugin == null) {
			throw new RuntimeException("the quartz plugin is not start, please check your Puff.xml");
		}
		this.scheduler = plugin.getScheduler();
	}

	private static class Inner {
		private static final QuartzManager INSTANCE = new QuartzManager();
	}

	public final static QuartzManager getInstance() {
		return Inner.INSTANCE;
	}

	@SuppressWarnings("unchecked")
	public void addJob(ScheduleJob job) throws SchedulerException {
		if (job == null || !ScheduleJob.STATUS_RUNNING.equals(job.getJobStatus())) {
			log.info("job is already running ...");
			return;
		}
		log.debug(scheduler + "...add job");
		TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		// 不存在，创建一个
		if (null == trigger) {
			Class<? extends Job> clazz = (Class<? extends Job>) Reflect.forName(job.getBeanClass());
			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getJobName(), job.getJobGroup()).build();
			jobDetail.getJobDataMap().put("scheduleJob", JsonUtil.toJson(job));
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
			trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();
			scheduler.scheduleJob(jobDetail, trigger);
		} else {
			// Trigger已存在，那么更新相应的定时设置
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
			// 按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
			// 按新的trigger重新设置job执行
			scheduler.rescheduleJob(triggerKey, trigger);
		}
	}

	public void addJob(String jobName, String jobGroup, String jobCronExp, String jobClassName, String description) {
		try {
			deleteJob(jobName, jobGroup);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		JobDetail jobDetail;
		CronTrigger trigger;
		String qrtzVersion = ((QuartzPlugin) Puff.getPlugin(QuartzPlugin.class)).getVersion();
		if ("1".equals(qrtzVersion)) {
			jobDetail = Reflect.on("org.quartz.JobDetail").create(jobName, jobGroup, Reflect.forName(jobClassName)).get();
			trigger = Reflect.on("org.quartz.CronTrigger").create(jobName, jobGroup, jobCronExp).get();
		} else {
			jobDetail = Reflect.on("org.quartz.JobBuilder").call("newJob", Reflect.forName(jobClassName)).call("withIdentity", jobName, jobGroup)
					.call("withDescription", description).call("build").get();
			Object temp = Reflect.on("org.quartz.TriggerBuilder").call("newTrigger").get();
			temp = Reflect.on(temp).call("withIdentity", jobName, jobGroup).get();
			temp = Reflect.on(temp).call("withSchedule", Reflect.on("org.quartz.CronScheduleBuilder").call("cronSchedule", jobCronExp).get()).get();
			trigger = Reflect.on(temp).call("build").get();
		}
		Date ft = Reflect.on(scheduler).call("scheduleJob", jobDetail, trigger).get();
		log.info(Reflect.on(jobDetail).call("getKey") + " has been scheduled to run at: " + ft + " " + "and repeat based on expression: "
				+ Reflect.on(trigger).call("getCronExpression"));
	}

	/**
	 * * 获取所有计划中的任务列表
	 * 
	 * @return
	 * 
	 * @throws SchedulerException
	 */
	public List<ScheduleJob> getAllJob() throws SchedulerException {
		GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
		Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
		for (JobKey jobKey : jobKeys) {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			for (Trigger trigger : triggers) {
				ScheduleJob job = new ScheduleJob();
				job.setJobName(jobKey.getName());
				job.setJobGroup(jobKey.getGroup());
				job.setDescription("触发器:" + trigger.getKey());
				Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
				job.setJobStatus(triggerState.name());
				if (trigger instanceof CronTrigger) {
					CronTrigger cronTrigger = (CronTrigger) trigger;
					job.setCronExpression(cronTrigger.getCronExpression());
				}
				jobList.add(job);
			}
		}
		return jobList;
	}

	/**
	 * 所有正在运行的job
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public List<ScheduleJob> getRunningJob() throws SchedulerException {
		List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>(executingJobs.size());
		for (JobExecutionContext executingJob : executingJobs) {
			ScheduleJob job = new ScheduleJob();
			JobDetail jobDetail = executingJob.getJobDetail();
			JobKey jobKey = jobDetail.getKey();
			Trigger trigger = executingJob.getTrigger();
			job.setJobName(jobKey.getName());
			job.setJobGroup(jobKey.getGroup());
			job.setDescription("触发器:" + trigger.getKey());
			Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
			job.setJobStatus(triggerState.name());
			if (trigger instanceof CronTrigger) {
				CronTrigger cronTrigger = (CronTrigger) trigger;
				job.setCronExpression(cronTrigger.getCronExpression());
			}
			jobList.add(job);
		}
		return jobList;
	}

	/**
	 * 暂停一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void pauseJob(String jobName, String jobGroup) throws SchedulerException {
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		scheduler.pauseJob(jobKey);
	}

	/**
	 * 恢复一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void resumeJob(String jobName, String jobGroup) throws SchedulerException {
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		scheduler.resumeJob(jobKey);
	}

	/**
	 * 删除一个job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		scheduler.pauseJob(jobKey);
		scheduler.deleteJob(jobKey);
	}

	/**
	 * 立即执行job
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void runJobNow(String jobName, String jobGroup) throws SchedulerException {
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		scheduler.triggerJob(jobKey);
	}

	/**
	 * 更新job时间表达式
	 * 
	 * @param scheduleJob
	 * @throws SchedulerException
	 */
	public void updateJobCron(String jobName, String jobGroup, String cronExpression) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
		scheduler.rescheduleJob(triggerKey, trigger);
	}

}
