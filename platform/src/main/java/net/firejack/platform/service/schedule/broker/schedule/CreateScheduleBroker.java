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
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.store.registry.IScheduleStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
	*This action allows an authorized client to create a new Schedule 
	*by providing all required data. Required parameters and their descriptions 
	*are provided in the parameters list below. 	
*/

@TrackDetails
@Component("createScheduleBroker")
public class CreateScheduleBroker extends OPFSaveBroker<ScheduleModel, Schedule, Schedule> {

    @Autowired
	private IScheduleStore store;


    @Override
    protected ScheduleModel convertToEntity(Schedule dto) {
        return factory.convertFrom(ScheduleModel.class, dto);
    }

    @Override
    protected Schedule convertToModel(ScheduleModel model) {
        return factory.convertTo(Schedule.class, model);
    }

    @Override
    protected void save(ScheduleModel scheduleModel) throws Exception {
        store.save(scheduleModel);
    }
}