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
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@TrackDetails
public class CloneRoleBroker extends ServiceBroker<ServiceRequest<NamedValues<Object>>, ServiceResponse<Role>> {

    public static final String PARAM_CLONED_ROLE_ID = "id";
    public static final String PARAM_NEW_ROLE_NAME = "name";

    @Autowired
    @Qualifier("roleStore")
    private IRoleStore roleStore;

    @Override
    protected ServiceResponse<Role> perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        Long roleId = (Long) request.getData().get(PARAM_CLONED_ROLE_ID);
        String roleName = (String) request.getData().get(PARAM_NEW_ROLE_NAME);
        ServiceResponse<Role> response;
        if (roleId == null) {
            response = new ServiceResponse<Role>("Id of the role to clone is not specified.", false);
        } else if (StringUtils.isBlank(roleName)) {
            response = new ServiceResponse<Role>();
        } else {
            RoleModel roleToClone = roleStore.findById(roleId);
            if (roleToClone == null) {
                response = new ServiceResponse<Role>("Role to clone is not found in the database.", false);
            } else {
                Role role = factory.convertTo(Role.class, roleToClone);
                role.setId(null);
                role.setName(roleName);
                role.setLookup(DiffUtils.lookup(role.getPath(), roleName));
                RoleModel newRoleModel = factory.convertFrom(RoleModel.class, role);
                roleStore.save(newRoleModel);
                role = factory.convertTo(Role.class, newRoleModel);
                response = new ServiceResponse<Role>(role, "Role is cloned successfully.", true);
            }
        }
        return response;
    }

}