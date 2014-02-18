package net.firejack.platform.service.schedule.broker.schedule;

import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IScheduleStore;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.SearchQuery;
import net.firejack.platform.core.utils.SortField;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Open Flame Platform
 * Date: Sun Mar 10 12:42:43 EET 2013
 */


@TrackDetails
@Component
public class AdvancedSearchScheduleBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Schedule>> {

    @Autowired
	private IScheduleStore store;

	@Override
	protected ServiceResponse<Schedule> perform(ServiceRequest<NamedValues> request) throws Exception {
		String queryParameters = (String) request.getData().get("queryParameters");
		Integer offset = (Integer) request.getData().get("offset");
		Integer limit = (Integer) request.getData().get("limit");
		String sortOrders = (String) request.getData().get("sortOrders");

		ObjectMapper mapper = new ObjectMapper();
		List<List<SearchQuery>> searchQueries = mapper.readValue(queryParameters, TypeFactory.parametricType(List.class, TypeFactory.parametricType(List.class, SearchQuery.class)));

		Integer total = store.advancedSearchCount(searchQueries);
		List<Schedule> vo = null;
		if (total > 0) {
			List<SortField> sortFields = mapper.readValue(sortOrders, new TypeReference<List<SortField>>(){});
		    List<ScheduleModel> models = store.advancedSearch(searchQueries, new Paging(offset, limit, sortFields));
		    vo = factory.convertTo(Schedule.class, models);
		}

		return new ServiceResponse<Schedule>(vo,"Action completed successfully.", true, total);
	}
}