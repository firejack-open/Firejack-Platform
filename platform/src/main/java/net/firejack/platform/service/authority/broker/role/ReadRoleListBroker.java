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

package net.firejack.platform.service.authority.broker.role;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@TrackDetails
@Component("readRoleListBrokerEx")
public class ReadRoleListBroker extends ListBroker<RoleModel, Role, NamedValues<Object>> {

    public static final String PARAM_REGISTRY_NODE_ID = "registryNodeId";
    public static final String PARAM_ID_TO_EXCLUDE = "exceptIds";
    public static final String PARAM_IS_GLOBAL = "isGlobal";

    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;

    @Override
    @SuppressWarnings("unchecked")
    protected List<RoleModel> getModelList(ServiceRequest<NamedValues<Object>> request)
            throws BusinessFunctionException {
        Long registryNodeId = (Long) request.getData().get(PARAM_REGISTRY_NODE_ID);
        SpecifiedIdsFilter<Long> filter = getFilter(true);
        filter.setAll(true);
        List<Long> exceptIds = (List<Long>) request.getData().get(PARAM_ID_TO_EXCLUDE);
        Boolean isGlobal = (Boolean) request.getData().get(PARAM_IS_GLOBAL);
        if (isGlobal == null) {
            isGlobal = false;
        }
        List<Long> exceptPermissionIds = ListUtils.removeNullableItems(exceptIds);
        if (exceptPermissionIds != null) {
            filter.getUnnecessaryIds().addAll(exceptPermissionIds);
        }
        List<RoleModel> roles = roleStore.findAllByRegistryNodeIdWithFilter(registryNodeId, filter, isGlobal);
        for (RoleModel role : roles) {
            role.setPermissions(null);
        }

        return roles;
    }

}