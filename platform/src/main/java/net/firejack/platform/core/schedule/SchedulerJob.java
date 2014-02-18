package net.firejack.platform.core.schedule;

import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.utils.OpenFlameSpringContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SchedulerJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        ScheduleModel scheduleModel = (ScheduleModel) jobDataMap.get("scheduleModel");

        ScheduleJobManager scheduleJobManager = OpenFlameSpringContext.getBean(ScheduleJobManager.class);
        scheduleJobManager.executeJob(scheduleModel, null);
    }

}
