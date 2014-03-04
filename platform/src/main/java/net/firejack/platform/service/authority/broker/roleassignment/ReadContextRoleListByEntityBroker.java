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

package net.firejack.platform.service.authority.broker.roleassignment;

import net.firejack.platform.api.authority.domain.AssignedRole;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@TrackDetails
@Component("readContextRoleListByEntityBrokerEx")
public class ReadContextRoleListByEntityBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<AssignedRole>> {

    public static final String PARAM_USER_ID = "PARAM_USER_ID";
    public static final String PARAM_OBJECT_ID = "PARAM_OBJECT_ID";
    public static final String PARAM_OBJECT_TYPE = "PARAM_OBJECT_TYPE";

    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;

    @Autowired
    @Qualifier("userStore")
    private IUserStore userStore;

    @Autowired
    @Qualifier("userRoleStore")
    private IUserRoleStore userRoleStore;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    private SpecifiedIdsFilter filter;

    @Override
    protected ServiceResponse<AssignedRole> perform(ServiceRequest<NamedValues<Object>> request)
		    throws Exception {
        Long objectId = (Long) request.getData().get(PARAM_OBJECT_ID);
        String objectType = (String) request.getData().get(PARAM_OBJECT_TYPE);
        List<RoleModel> roles = new ArrayList<RoleModel>();
        RegistryNodeModel registryNode = registryNodeStore.findByLookup(objectType); //TODO need to review next three lines, may be better use lookup instead class
        if (registryNode != null) {
            roles = roleStore.findAllByRegistryNodeTypeWithFilter(registryNode.getClass(), getFilter());
        }
        for (RoleModel role : roles) {
            role.setPermissions(null);
        }
        List<AssignedRole> assignedRoleList = factory.convertTo(AssignedRole.class, roles);

        List<Long> assignedRoleIds = new ArrayList<Long>();
        Long userId = (Long) request.getData().get(PARAM_USER_ID);
        if (userId != null) {
            List<UserRoleModel> assignedRoles = userRoleStore.findContextRolesByUserIdAndRegistryNodeId(userId, objectId, objectType);
            for (UserRoleModel assignedRole : assignedRoles) {
                assignedRoleIds.add(assignedRole.getRole().getId());
            }
        }
        for (AssignedRole assignedRole : assignedRoleList) {
            boolean assigned = assignedRoleIds.contains(assignedRole.getId());
            assignedRole.setAssigned(assigned);
        }
        return new ServiceResponse<AssignedRole>(assignedRoleList);
    }

    public SpecifiedIdsFilter getFilter() {
        SpecifiedIdsFilter filter = getFilter(true);
        if (filter.getAll() == null) {
            filter.setAll(true);
        }
        return filter;
    }

    public SpecifiedIdsFilter getFilter(boolean isInitialize) {
        if (this.filter == null || isInitialize) {
            initFilter();
        }
        return this.filter;
    }

    private void initFilter() {
        this.filter = new SpecifiedIdsFilter();
    }
}