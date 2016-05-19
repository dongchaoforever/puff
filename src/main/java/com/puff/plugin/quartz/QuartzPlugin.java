package com.puff.plugin.quartz;

import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.puff.exception.ExceptionUtil;
import com.puff.framework.utils.IOUtil;
import com.puff.framework.utils.PathUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;
import com.puff.plugin.Plugin;

public class QuartzPlugin implements Plugin {
	private static final String JOB = "job";
	private final Log logger = LogFactory.get(getClass());
	private Map<Job, String> jobs = new LinkedHashMap<Job, String>();
	private String version;
	private SchedulerFactory sf;
	private Scheduler scheduler;
	private String jobConfig;
	private String confConfig;
	private Map<String, String> jobProp;
	private int startupDelay;

	public SchedulerFactory getSchedulerFactory() {
		return sf;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public String getVersion() {
		return version;
	}

	public int getStartupDelay() {
		return startupDelay;
	}

	@Override
	public void init(Properties prop) {
		version = prop.getProperty("quartz.version", "2");
		confConfig = prop.getProperty("quartz.config");
		jobConfig = prop.getProperty("quartz.jobConfig");
		try {
			startupDelay = Integer.parseInt(prop.getProperty("quartz.startupDelay", "30"));
		} catch (NumberFormatException e) {
			startupDelay = 30;
		}
	}

	@Override
	public boolean start() {
		loadJobsFromProperties();
		startJobs();
		return true;
	}

	private void startJobs() {
		try {
			if (StringUtil.notBlank(confConfig)) {
				sf = new StdSchedulerFactory(confConfig);
			} else {
				sf = new StdSchedulerFactory();
			}
			scheduler = sf.getScheduler();
		} catch (SchedulerException e) {
			ExceptionUtil.throwRuntime(e);
		}
		for (Map.Entry<Job, String> entry : jobs.entrySet()) {
			Job job = entry.getKey();
			String jobClassName = job.getClass().getName();
			String jobCronExp = entry.getValue();
			JobDetail jobDetail;
			CronTrigger trigger;
			if ("1".equals(version)) {
				jobDetail = Reflect.on("org.quartz.JobDetail").create(jobClassName, jobClassName, job.getClass()).get();
				trigger = Reflect.on("org.quartz.CronTrigger").create(jobClassName, jobClassName, jobCronExp).get();
			} else {
				jobDetail = Reflect.on("org.quartz.JobBuilder").call("newJob", job.getClass()).call("withIdentity", jobClassName, jobClassName).call("build").get();
				Object temp = Reflect.on("org.quartz.TriggerBuilder").call("newTrigger").get();
				temp = Reflect.on(temp).call("withIdentity", jobClassName, jobClassName).get();
				temp = Reflect.on(temp).call("withSchedule", Reflect.on("org.quartz.CronScheduleBuilder").call("cronSchedule", jobCronExp).get()).get();
				trigger = Reflect.on(temp).call("build").get();
			}
			Date ft = Reflect.on(scheduler).call("scheduleJob", jobDetail, trigger).get();
			logger.info(Reflect.on(jobDetail).call("getKey") + " has been scheduled to run at: " + ft + " " + "and repeat based on expression: "
					+ Reflect.on(trigger).call("getCronExpression"));
		}
		try {
			startScheduler(scheduler, startupDelay);
		} catch (SchedulerException e) {
			logger.error("schedule start fail ", e);
			ExceptionUtil.throwRuntime(e);
		}
	}

	protected void startScheduler(final Scheduler scheduler, final int startupDelay) throws SchedulerException {
		if (startupDelay <= 0) {
			logger.info("Starting Quartz Scheduler now");
			scheduler.start();
		} else {
			logger.info("Will start Quartz Scheduler [" + scheduler.getSchedulerName() + "] in " + startupDelay + " seconds");
			Thread schedulerThread = new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(startupDelay * 1000);
					} catch (InterruptedException ex) {
					}
					logger.info("Starting Quartz Scheduler now, after delay of " + startupDelay + " seconds");
					try {
						scheduler.start();
					} catch (SchedulerException ex) {
						ExceptionUtil.throwRuntime(ex);
					}
				}
			};
			schedulerThread.setName("Quartz Scheduler [" + scheduler.getSchedulerName() + "]");
			schedulerThread.setDaemon(true);
			schedulerThread.start();
		}
	}

	private void loadJobsFromProperties() {
		if (StringUtil.blank(jobConfig)) {
			return;
		}
		jobProp = readProperties(jobConfig);
		Set<Map.Entry<String, String>> entries = jobProp.entrySet();
		for (Map.Entry<String, String> entry : entries) {
			String key = entry.getKey();
			if (!key.endsWith(JOB) || !isEnableJob(enable(key))) {
				continue;
			}
			String jobClassName = jobProp.get(key);
			String jobCronExp = jobProp.get(cronKey(key));
			Class<Job> job = Reflect.on(jobClassName).get();
			try {
				jobs.put(job.newInstance(), jobCronExp);
			} catch (Exception e) {
				ExceptionUtil.throwRuntime(e);
			}
		}
	}

	public static Map<String, String> readProperties(String resourceName) {

		InputStream inputStream = PathUtil.fromClassPath(resourceName);
		Properties properties = new Properties();
		Map<String, String> map = new HashMap<String, String>();
		try {
			properties.load(inputStream);
			for (Enumeration<?> elements = properties.propertyNames(); elements.hasMoreElements();) {
				String key = (String) elements.nextElement();
				map.put(key, properties.getProperty(key));
			}
		} catch (Exception e) {

		} finally {
			IOUtil.close(inputStream);
		}
		return map;
	}

	private String enable(String key) {
		return key.substring(0, key.lastIndexOf(JOB)) + "enable";
	}

	private String cronKey(String key) {
		return key.substring(0, key.lastIndexOf(JOB)) + "cron";
	}

	private boolean isEnableJob(String enableKey) {
		Object enable = jobProp.get(enableKey);
		if (enable != null && "false".equalsIgnoreCase((enable + "").trim())) {
			return false;
		}
		return true;
	}

	@Override
	public boolean stop() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			logger.error("stop quartz fail: ", e);
		}
		return true;
	}
}
