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
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@TrackDetails
public class ReleaseContextUserRolesBroker extends ServiceBroker<ServiceRequest<UserRole>, ServiceResponse> {

    @Autowired
    private IRoleStore roleStore;
    @Autowired
    private IUserRoleStore userRoleStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<UserRole> request) throws Exception {
        List<UserRole> userRoles = request.getDataList();
        ServiceResponse response;
        if (userRoles == null || userRoles.isEmpty()) {
            response = new ServiceResponse<UserRole>("No user role information specified.", false);
        } else {
            OPFContext context = OPFContext.getContext();
            if (context == null || context.getPrincipal().isGuestPrincipal()) {
                response = new ServiceResponse<UserRole>("Guest user is not authorized to release context roles.", false);
            } else {
                Set<String> lookupList = new HashSet<String>();
                LinkedList<UserRole> userRolesValidated = new LinkedList<UserRole>();
                for (UserRole userRole : userRoles) {
                    Role role = userRole.getRole();
                    BaseUser user = userRole.getUser();
                    if (role != null && StringUtils.isNotBlank(role.getLookup()) && user != null &&
                            user.getId() != null && StringUtils.isNotBlank(userRole.getTypeLookup()) &&
                            (userRole.getModelId() != null || StringUtils.isNotBlank(userRole.getComplexPK()))) {
                        userRolesValidated.add(userRole);
                        lookupList.add(role.getLookup());
                    }
                }
                if (!lookupList.isEmpty()) {
                    LinkedList<Criterion> restrictions = new LinkedList<Criterion>();
                    restrictions.add(Restrictions.in("lookup", lookupList));
                    List<Object[]> roles = roleStore.searchWithProjection(restrictions,
                            Projections.projectionList()
                                    .add(Projections.property("lookup"))
                                    .add(Projections.id()), null);
                    restrictions.clear();
                    Map<String, Long> rolesMap = new HashMap<String, Long>();
                    for (Object[] roleModelData : roles) {
                        rolesMap.put((String) roleModelData[0], (Long) roleModelData[1]);
                    }
                    LinkedList<UserRoleModel> userRoleList = new LinkedList<UserRoleModel>();
                    for (UserRole userRole : userRolesValidated) {
                        Role role = userRole.getRole();
                        if (role != null && StringUtils.isNotBlank(role.getLookup())) {
                            Long roleId = rolesMap.get(role.getLookup());
                            if (roleId != null) {
                                restrictions.add(Restrictions.eq("user.id", userRole.getUser().getId()));
                                restrictions.add(Restrictions.eq("role.id", roleId));
                                restrictions.add(Restrictions.eq("type", userRole.getTypeLookup()));
                                if (userRole.getModelId() == null) {
                                    restrictions.add(Restrictions.eq("externalId", userRole.getComplexPK()));
                                } else {
                                    restrictions.add(Restrictions.eq("internalId", userRole.getModelId()));
                                }
                                List<UserRoleModel> foundUserRoles = userRoleStore.search(restrictions, null, null, false);
                                if (!foundUserRoles.isEmpty()) {
                                    userRoleList.add(foundUserRoles.get(0));
                                }
                                restrictions.clear();
                            }
                        }
                    }
                    userRoleStore.deleteAll(userRoleList);
                }
                response = new ServiceResponse<UserRole>("Success", true);
            }
        }
        return response;
    }

}