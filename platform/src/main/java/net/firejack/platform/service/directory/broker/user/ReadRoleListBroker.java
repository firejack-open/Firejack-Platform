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

package net.firejack.platform.service.directory.broker.user;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.utils.ListUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("readRoleListBroker")
public class ReadRoleListBroker extends ListBroker<RoleModel, Role, NamedValues> {

	@Autowired
	private IRoleStore store;

	@Override
	protected List<RoleModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
		Long registryNodeId = (Long) request.getData().get("registryNodeId");
		List<Long> exceptIds = (List<Long>) request.getData().get("exceptIds");
		Boolean isGlobal = (Boolean) request.getData().get("isGlobal");
		SpecifiedIdsFilter<Long> filter = getFilter(true);

		filter.setAll(true);
		if (isGlobal == null) {
			isGlobal = false;
		}
		List<Long> exceptPermissionIds = ListUtils.removeNullableItems(exceptIds);
		filter.getUnnecessaryIds().addAll(exceptPermissionIds);
		List<RoleModel> roles = store.findAllByRegistryNodeIdWithFilter(registryNodeId, filter, isGlobal);
		for (RoleModel role : roles) {
			role.setPermissions(null);
		}
		return roles;
	}
}
