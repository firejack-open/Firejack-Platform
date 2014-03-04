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

package net.firejack.platform.service.authority.broker.sts;

import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.cache.CacheProcessor;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.service.authority.utils.OpenIDProcessor;
import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;
import net.firejack.platform.web.security.session.UserSessionManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@TrackDetails
@Component("openIDAuthenticationBrokerEx")
public class OpenIDAuthenticationBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<AuthenticationToken>> {

    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_OPEN_ID_ATTRIBUTES = "openIdAttributes";
    public static final String PARAM_BROWSER_IP_ADDRESS = "browserIpAddress";

    @Autowired
    @Qualifier("cacheProcessor")
    private CacheProcessor cacheProcessor;

    @Autowired
    private OpenIDProcessor openIDProcessor;
    @Autowired
    private IRoleStore roleStore;

    @Override
    protected ServiceResponse<AuthenticationToken> perform(ServiceRequest<NamedValues<Object>> request)
		    throws Exception {
        String email = (String) request.getData().get(PARAM_EMAIL);
        String browserIpAddress = (String) request.getData().get(PARAM_BROWSER_IP_ADDRESS);
        UserModel userModel = openIDProcessor.findUserByEmail(email);
        List<RoleModel> userRoles;
        if (userModel == null) {
            @SuppressWarnings("unchecked")
            Map<SupportedOpenIDAttribute, String> mappedAttributes =
                    (Map<SupportedOpenIDAttribute, String>) request.getData().get(PARAM_OPEN_ID_ATTRIBUTES);
            Tuple<UserModel, List<RoleModel>> userRolesTuple = openIDProcessor.createUserFromOpenIDAttributes(mappedAttributes);
            userModel = userRolesTuple.getKey();
            userRoles = userRolesTuple.getValue();
        } else {
            userRoles = roleStore.findRolesByUserId(userModel.getId());
        }
        List<Long> roleIds = new ArrayList<Long>();
        List<Role> roles = new ArrayList<Role>();
        if (userRoles != null) {
            for (RoleModel roleModel : userRoles) {
                Role role = new Role();
                role.setId(roleModel.getId());
                role.setName(roleModel.getName());
                role.setPath(roleModel.getPath());
                role.setLookup(roleModel.getLookup());
                role.setParentId(roleModel.getParent().getId());

                roleIds.add(roleModel.getId());
                roles.add(role);
            }
        }
        UserSessionManager userSessionManager = UserSessionManager.getInstance();
        String token = userSessionManager.openUserSession(userModel, cacheProcessor, roleIds, browserIpAddress);

        User user = factory.convertTo(User.class, userModel);
        user.setRoles(roles);

        AuthenticationToken authenticationToken = new AuthenticationToken(token, user);
        return new ServiceResponse<AuthenticationToken>(authenticationToken, "OpenId user authenticated successfully", true);
    }
}