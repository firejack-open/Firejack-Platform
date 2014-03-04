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

import net.firejack.platform.api.schedule.domain.ScheduleHistory;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.schedule.ScheduleHistoryModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IScheduleHistoryStore;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("readAllScheduleHistoryByScheduleBroker")
public class ReadAllScheduleHistoryByScheduleBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<ScheduleHistory>> {

    @Autowired
	private IScheduleHistoryStore store;

	@Override
	protected ServiceResponse<ScheduleHistory> perform(ServiceRequest<NamedValues> request) throws Exception {
		Long scheduleId = (Long) request.getData().get("scheduleId");
		Integer offset = (Integer) request.getData().get("offset");
		Integer limit = (Integer) request.getData().get("limit");
		String sortColumn = (String) request.getData().get("sortColumn");
		String sortDirection = (String) request.getData().get("sortDirection");

		Integer total = store.countBySchedule(scheduleId);
		List<ScheduleHistory> vo = null;
		if (total > 0) {
			List<ScheduleHistoryModel> models = store.findBySchedule(scheduleId, new Paging(offset, limit, sortColumn, sortDirection));
			vo = factory.convertTo(ScheduleHistory.class, models);
		}
		return new ServiceResponse<ScheduleHistory>(vo, "Action completed successfully.", true, total);
	}

}
