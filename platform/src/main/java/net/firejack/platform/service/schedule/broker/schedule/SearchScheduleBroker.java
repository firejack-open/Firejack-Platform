package net.firejack.platform.service.schedule.broker.schedule;

import java.util.*;

import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IScheduleStore;
import net.firejack.platform.core.utils.ListUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.domain.NamedValues;

/**
 * Created by Open Flame Platform
 * Date: Sun Mar 10 12:42:43 EET 2013
 */

/**
	*This action allows an authorized client to search for existing Schedules 
	*by providing search terms and searchable parameters. Required 
	*parameters and their descriptions are provided in the parameters 
	*list below. 	
*/

@TrackDetails
@Component("searchScheduleBroker")
public class SearchScheduleBroker extends ListBroker<ScheduleModel, Schedule, NamedValues<Object>> {

	@Autowired
	private IScheduleStore store;
	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	@Override
	protected List<ScheduleModel> getModelList(ServiceRequest<NamedValues<Object>> request) throws BusinessFunctionException {
		Long parentId = (Long) request.getData().get("parentId");
		String term = (String) request.getData().get("term");
		List<Long> exceptIds = (List<Long>) request.getData().get("exceptIds");
		SpecifiedIdsFilter<Long> filter = getFilter();

		if (exceptIds != null && !exceptIds.isEmpty()) {
			List<Long> exceptPermissionIds = ListUtils.removeNullableItems(exceptIds);
			filter.getUnnecessaryIds().addAll(exceptPermissionIds);
		}
		List<ScheduleModel> configs;
		if (parentId != null && parentId > 0) {
			List<Long> registryNodeIds = new ArrayList<Long>();
			List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
			registryNodeStore.findCollectionChildrenIds(registryNodeIds, parentId, collectionArrayIds);
			registryNodeIds.add(parentId);
			configs = store.findAllBySearchTermWithFilter(registryNodeIds, term, filter);
		} else {
			configs = store.findAllBySearchTermWithFilter(term, filter);
		}
		return configs;
	}
}