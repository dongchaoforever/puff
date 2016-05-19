package com.puff.plugin.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class QuartzJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

	}

	public abstract boolean startCheck(JobExecutionContext context, ScheduleJob job);

}
