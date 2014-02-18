package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;

import java.util.List;

/**
	*The Schedule items for execute any associated actions by schedule 
	*cron expression 	
*/

public interface IScheduleStore extends IRegistryNodeStore<ScheduleModel> {

    List<ScheduleModel> findAll();

    List<ScheduleModel> findAllByLikeLookupPrefix(String lookupPrefix);

    void deleteAllByRegistryNodeId(Long registryNodeId);

    List<ScheduleModel> findAllBySearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter);

    List<ScheduleModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter);

    void deleteByActionId(Long actionId);

}