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

import net.firejack.platform.api.schedule.domain.Schedule;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.schedule.ScheduleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.schedule.ScheduleJobManager;
import net.firejack.platform.core.schedule.ScheduleJobStatus;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IScheduleStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("readScheduleListByRegistryNodeBroker")
public class ReadScheduleListByRegistryNodeBroker extends ListBroker<ScheduleModel, Schedule, SimpleIdentifier<Long>> {

	@Autowired
	private IScheduleStore store;
	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;
    @Autowired
    private ScheduleJobManager scheduleJobManager;

	@Override
	protected List<ScheduleModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> request) throws BusinessFunctionException {
		Long registryNodeId = request.getData().getIdentifier();

		List<Long> registryNodeIds = new ArrayList<Long>();
		List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
		registryNodeStore.findCollectionChildrenIds(registryNodeIds, registryNodeId, collectionArrayIds);
		registryNodeIds.add(registryNodeId);

		return store.findAllByParentIdsWithFilter(registryNodeIds, getFilter(), null);
	}

    @Override
    protected List<Schedule> convertToDTOs(List<ScheduleModel> entities) {
        List<Schedule> schedules = super.convertToDTOs(entities);
        for (Schedule schedule : schedules) {
            ScheduleJobStatus jobStatus = scheduleJobManager.getJobStatus(schedule.getId());
            if (jobStatus != null) {
                schedule.setMessage(jobStatus.getMessage());
                schedule.setPercent(jobStatus.getPercents());
            }
        }
        return schedules;
    }
}
