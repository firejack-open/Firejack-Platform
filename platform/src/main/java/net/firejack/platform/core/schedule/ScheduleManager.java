/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.core.schedule;

import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.store.registry.IScheduleStore;
import net.firejack.platform.model.event.CompleteInitEvent;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleManager implements ApplicationListener<CompleteInitEvent> {

    public static final String SCHEDULER_GROUP = "OPENFLAME";

    private static final Logger logger = Logger.getLogger(ScheduleManager.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private IScheduleStore scheduleStore;


    @Override
    public void onApplicationEvent(CompleteInitEvent event) {
        initScheduleJobs();
    }

    public void initScheduleJobs() {
        List<ScheduleModel> scheduleModels = scheduleStore.findAll();

        try {
            scheduler.start();
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }

        for (ScheduleModel scheduleModel : scheduleModels) {
            createScheduleJob(scheduleModel);
        }
    }

    public void createScheduleJob(ScheduleModel scheduleModel) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("scheduleModel", scheduleModel);

        JobDetail jobDetail = new JobDetail(getJobName(scheduleModel), SCHEDULER_GROUP, SchedulerJob.class);
        jobDetail.setJobDataMap(jobDataMap);

        try {
            Trigger trigger = new CronTrigger(
                    getTriggerName(scheduleModel),
                    SCHEDULER_GROUP,
                    getJobName(scheduleModel), SCHEDULER_GROUP,
                    scheduleModel.getCronExpression());

            scheduler.scheduleJob(jobDetail, trigger);

            if (!scheduleModel.getActive()) {
                scheduler.pauseJob(getJobName(scheduleModel), SCHEDULER_GROUP);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void updateScheduleJob(ScheduleModel scheduleModel) {
        deleteScheduleJob(scheduleModel);
        createScheduleJob(scheduleModel);
    }

    public void deleteScheduleJob(ScheduleModel scheduleModel) {
        try {
            scheduler.deleteJob(getJobName(scheduleModel), SCHEDULER_GROUP);
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String getJobName(ScheduleModel scheduleModel) {
        return "Job" + scheduleModel.getId();
    }

    private String getTriggerName(ScheduleModel scheduleModel) {
        return "Trigger" + scheduleModel.getId();
    }
}
