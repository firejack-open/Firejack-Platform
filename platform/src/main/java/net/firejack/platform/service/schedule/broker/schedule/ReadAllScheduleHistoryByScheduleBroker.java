/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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
