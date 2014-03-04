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

package net.firejack.platform.service.directory.broker.group;

import net.firejack.platform.api.directory.domain.Group;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IGroupStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.ListUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("searchGroupListBroker")
public class SearchGroupListBroker extends ListBroker<GroupModel, Group, NamedValues> {

	@Autowired
	private IGroupStore store;
	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	@Override
	protected List<GroupModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
		Long registryNodeId = (Long) request.getData().get("registryNodeId");
		String term = (String) request.getData().get("term");
		List<Long> exceptIds = (List<Long>) request.getData().get("exceptIds");
		SpecifiedIdsFilter<Long> filter = getFilter();

		if (exceptIds != null && !exceptIds.isEmpty()) {
			List<Long> exceptPermissionIds = ListUtils.removeNullableItems(exceptIds);
			filter.getUnnecessaryIds().addAll(exceptPermissionIds);
		}
		List<GroupModel> groups;
		if (registryNodeId != null) {
			List<Long> registryNodeIds = new ArrayList<Long>();
			List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
			registryNodeStore.findCollectionChildrenIds(registryNodeIds, registryNodeId, collectionArrayIds);
			registryNodeIds.add(registryNodeId);
			groups = store.findAllBySearchTermWithFilter(registryNodeIds, term, filter);
		} else {
			groups = store.findAllBySearchTermWithFilter(term, filter);
		}
		for (GroupModel group : groups) {
			group.setDirectory(null);
		}
		return groups;
	}
}
