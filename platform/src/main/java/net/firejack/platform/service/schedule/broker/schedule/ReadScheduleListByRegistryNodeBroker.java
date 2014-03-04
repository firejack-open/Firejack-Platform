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
