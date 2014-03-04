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

import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


@Component
@TrackDetails
public class CheckUserIsOwnerBroker extends ServiceBroker
        <ServiceRequest<UserRole>, ServiceResponse<SimpleIdentifier<Boolean>>> {

    @Autowired
    private IUserRoleStore userRoleStore;

    @Override
    protected ServiceResponse<SimpleIdentifier<Boolean>> perform(ServiceRequest<UserRole> request) throws Exception {
        ServiceResponse<SimpleIdentifier<Boolean>> response;
        UserRole userRole = request.getData();
        if (userRole == null || StringUtils.isBlank(userRole.getTypeLookup()) ||
                (userRole.getModelId() == null && StringUtils.isBlank(userRole.getComplexPK()))) {
            response = new ServiceResponse<SimpleIdentifier<Boolean>>("Not enough input parameters are specified.", false);
        } else {
            OPFContext context;
            try {
                context = OPFContext.getContext();
            } catch (ContextLookupException e) {
                context = null;
            }
            if (context == null || context.getPrincipal().isGuestPrincipal()) {
                response = new ServiceResponse<SimpleIdentifier<Boolean>>("Guest user could not be an object owner.", false);
            } else {
                Map<String, String> aliases = new HashMap<String, String>();
                aliases.put("role", "role");
                String roleLookup = userRole.getTypeLookup() + ".owner";
                Long currentUserId = context.getPrincipal().getUserInfoProvider().getId();

                LinkedList<Criterion> restrictions = new LinkedList<Criterion>();
                restrictions.add(Restrictions.eq("type", userRole.getTypeLookup()));
                restrictions.add(Restrictions.eq("user.id", currentUserId));
                restrictions.add(Restrictions.eq("role.lookup", roleLookup));
                if (userRole.getModelId() == null) {
                    restrictions.add(Restrictions.eq("externalId", userRole.getComplexPK()));
                } else {
                    restrictions.add(Restrictions.eq("internalId", userRole.getModelId()));
                }
                Integer recordsCount = userRoleStore.searchCount(restrictions, aliases, false);
                SimpleIdentifier<Boolean> isOwner = new SimpleIdentifier<Boolean>(recordsCount != null && recordsCount > 0);

                response = new ServiceResponse<SimpleIdentifier<Boolean>>(isOwner, "Success", true);
            }
        }
        return response;
    }

}