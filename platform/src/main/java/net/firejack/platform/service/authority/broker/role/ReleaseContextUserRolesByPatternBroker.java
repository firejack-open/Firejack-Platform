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
import net.firejack.platform.api.directory.domain.BaseUser;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Component
@TrackDetails
public class ReleaseContextUserRolesByPatternBroker extends ServiceBroker<ServiceRequest<UserRole>, ServiceResponse> {

    @Autowired
    private IUserRoleStore userRoleStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<UserRole> request) throws Exception {
        UserRole userRolePattern = request.getData();
        ServiceResponse response;
        if (userRolePattern == null) {
            response = new ServiceResponse<UserRole>("User role pattern information is not specified.", false);
        } else {
            Map<String, String> aliases = null;
            LinkedList<Criterion> restrictions = new LinkedList<Criterion>();
            Role role = userRolePattern.getRole();
            if (role != null) {
                if (role.getId() != null) {
                    restrictions.add(Restrictions.eq("role.id", role.getId()));
                } else if (StringUtils.isNotBlank(role.getLookup())) {
                    aliases = new HashMap<String, String>();
                    aliases.put("role", "role");
                    restrictions.add(Restrictions.eq("role.lookup", role.getLookup()));
                }
            }
            if (userRolePattern.getModelId() != null) {
                restrictions.add(Restrictions.eq("internalId", userRolePattern.getModelId()));
            } else if (StringUtils.isNotBlank(userRolePattern.getComplexPK())) {
                restrictions.add(Restrictions.eq("externalId", userRolePattern.getComplexPK()));
            }
            if (StringUtils.isNotBlank(userRolePattern.getTypeLookup())) {
                restrictions.add(Restrictions.eq("type", userRolePattern.getTypeLookup()));
            }
            BaseUser user = userRolePattern.getUser();
            if (user != null) {
                if (user.getId() != null) {
                    restrictions.add(Restrictions.eq("user.id", user.getId()));
                } else if (StringUtils.isNotBlank(user.getUsername())) {
                    if (aliases == null) {
                        aliases = new HashMap<String, String>();
                    }
                    aliases.put("user", "user");
                    restrictions.add(Restrictions.eq("user.username", user.getUsername()));
                }
            }
            List<UserRoleModel> userRolesFound = userRoleStore.search(restrictions, aliases, null, false);
            if (userRolesFound.isEmpty()) {
                response = new ServiceResponse("No user roles for specified pattern were found.", true);
            } else {
                userRoleStore.deleteAll(userRolesFound);
                response = new ServiceResponse("User Roles found and deleted by specified pattern.", true);
            }
        }
        return response;
    }

}