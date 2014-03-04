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

package net.firejack.platform.service.schedule.broker.schedule;


import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.schedule.ScheduleJobManager;
import net.firejack.platform.core.schedule.ScheduleJobStatus;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("getScheduleProgressStatusBroker")
public class GetScheduleProgressStatusBroker extends ServiceBroker<ServiceRequest<NamedValues<List<Long>>>, ServiceResponse<Schedule>> {

    @Autowired
    private ScheduleJobManager scheduleJobManager;

    @Override
    protected ServiceResponse<Schedule> perform(ServiceRequest<NamedValues<List<Long>>> request) throws Exception {
        List<Long> scheduleIds = request.getData().get("scheduleIds");

        List<Schedule> schedules = new ArrayList<Schedule>();
        for (Long scheduleId : scheduleIds) {
            ScheduleJobStatus jobStatus = scheduleJobManager.getJobStatus(scheduleId);
            if (jobStatus != null) {
                Schedule schedule = new Schedule();
                schedule.setId(scheduleId);
                schedule.setMessage(jobStatus.getMessage());
                schedule.setPercent(jobStatus.getPercents());
                schedules.add(schedule);
            }
        }
        return new ServiceResponse<Schedule>(schedules, "", true);
    }

}
