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

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.RoleStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@TrackDetails
public class AllowInstantSecurityBroker extends ServiceBroker<ServiceRequest<NamedValues<Object>>, ServiceResponse> {

    public static final String PARAM_ENTITY_LOOKUP = "entityLookup";
    public static final String PARAM_SECURITY_ENABLED = "securityEnabled";
    private static final String MSG_SECURITY_VIOLATION =
            "Current User is not permitted to change the Instant Security settings for the package.";

    @Autowired
    private RoleStore roleStore;

    @Autowired
    private IEntityStore entityStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        String entityLookup = (String) request.getData().get(PARAM_ENTITY_LOOKUP);
        Boolean securityEnabled = (Boolean) request.getData().get(PARAM_SECURITY_ENABLED);
        ServiceResponse response;
        if (StringUtils.isBlank(entityLookup)) {
            response = new ServiceResponse("Entity lookup is not specified.", false);
        } else if (securityEnabled == null) {
            response = new ServiceResponse("Security Enabled flag is not specified.", false);
        } else {
            OPFContext context;
            try {
                context = OPFContext.getContext();
            } catch (ContextLookupException e) {
                context = null;
            }
            if (context == null || context.getPrincipal().isGuestPrincipal()) {
                response = new ServiceResponse(MSG_SECURITY_VIOLATION, false);
            } else {
                CacheManager cacheManager = CacheManager.getInstance();
                Tuple<User, List<Long>> userRoles = cacheManager.getUserInfo(context.getSessionToken());
                if (userRoles == null || userRoles.getValue() == null || userRoles.getValue().isEmpty()) {
                    response = new ServiceResponse(MSG_SECURITY_VIOLATION, false);
                } else {
                    EntityModel entityModel = entityStore.findByLookup(entityLookup);
                    if (entityModel == null) {
                        response = new ServiceResponse("There is no entity for specified lookup value.", false);
                    } else {
                        String packageAdminRole = StringUtils.getPackageLookup(entityLookup) + ".admin";
                        RoleModel adminRole = roleStore.findByLookup(packageAdminRole);
                        if (adminRole == null || !userRoles.getValue().contains(adminRole.getId())) {
                            response = new ServiceResponse(MSG_SECURITY_VIOLATION, false);
                        } else {
                            entityModel.setSecurityEnabled(securityEnabled);
                            entityStore.save(entityModel);
                            response = new ServiceResponse("Security Enabled flag is set.", true);
                        }
                    }
                }
            }
        }
        return response;
    }

}