package net.firejack.platform.service.schedule.broker.schedule;

import java.util.*;

import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.store.registry.IScheduleStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import net.firejack.platform.core.utils.Paging;
import org.springframework.stereotype.Component;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.domain.NamedValues;

/**
 * Created by Open Flame Platform
 * Date: Sun Mar 10 12:42:43 EET 2013
 */

/**
	*This action allows an authorized client to read all existing Schedules. 
	*Required parameters and their descriptions are provided in 
	*the parameters list below. 	
*/

@TrackDetails
@Component("readAllScheduleBroker")
public class ReadAllScheduleBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Schedule>> {

    @Autowired
	private IScheduleStore store;

	@Override
	protected ServiceResponse<Schedule> perform(ServiceRequest<NamedValues> request) throws Exception {
		Integer offset = (Integer) request.getData().get("offset");
		Integer limit = (Integer) request.getData().get("limit");
		String sortColumn = (String) request.getData().get("sortColumn");
		String sortDirection = (String) request.getData().get("sortDirection");

		Integer total = store.searchCount(null, null);
		List<Schedule> vo = null;
		if (total > 0) {
			List<ScheduleModel> models = store.search(null, new Paging(offset, limit, sortColumn, sortDirection));
			vo = factory.convertTo(Schedule.class, models);
		}
		return new ServiceResponse<Schedule>(vo,"Action completed successfully.", true, total);
	}
}