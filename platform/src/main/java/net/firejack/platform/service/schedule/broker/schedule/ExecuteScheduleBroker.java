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
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.schedule.ScheduleJobManager;
import net.firejack.platform.core.store.registry.IScheduleStore;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
	*This action allows an authorized client to create a new Schedule 
	*by providing all required data. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

@TrackDetails
@Component("executeScheduleBroker")
public class ExecuteScheduleBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<Schedule>> {

    @Autowired
	private IScheduleStore store;
    @Autowired
    private ScheduleJobManager scheduleJobManager;

    @Override
    protected ServiceResponse<Schedule> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
        Long scheduleId = request.getData().getIdentifier();

        ServiceResponse<Schedule> scheduleServiceResponse;
        ScheduleModel scheduleModel = store.findById(scheduleId);
        if (scheduleModel != null) {
            Long userOriginalCallerId = OPFContext.getContext().getPrincipal().getUserInfoProvider().getId();
            scheduleJobManager.executeJob(scheduleModel, userOriginalCallerId);

            Schedule schedule = factory.convertTo(Schedule.class, scheduleModel);
            scheduleServiceResponse = new ServiceResponse<Schedule>(schedule, "Successfully started to execute.", true);
        } else {
            scheduleServiceResponse = new ServiceResponse<Schedule>("Has not been found ScheduleJob by id:" + scheduleId, false);
        }
        return scheduleServiceResponse;
    }

}