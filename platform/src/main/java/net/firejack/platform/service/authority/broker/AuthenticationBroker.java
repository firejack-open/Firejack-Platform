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

package net.firejack.platform.service.authority.broker;


import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.cache.CacheProcessor;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.model.user.BaseUserModel;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IServerStore;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.service.authority.utils.ISecurityAuthenticator;
import net.firejack.platform.web.security.session.UserSessionManager;
import net.firejack.platform.web.security.x509.KeyUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@TrackDetails
@Component("authenticationBrokerEx")
public class AuthenticationBroker extends ServiceBroker
        <ServiceRequest<NamedValues<String>>, ServiceResponse<AuthenticationToken>> {

    public static final String PARAM_AUTH_USERNAME = "username";
    public static final String PARAM_AUTH_PASSWORD = "password";
    public static final String PARAM_PACKAGE_LOOKUP = "lookup";
    public static final String PARAM_SERVER_NAME = "name";
    public static final String PARAM_AUTH_CERT = "cert";
    public static final String PARAM_BROWSER_IP_ADDRESS = "browserIpAddress";

    @Autowired
    @Qualifier("directoryListAuthenticator")
    private ISecurityAuthenticator authenticator;

    @Autowired
    private IServerStore serverStore;
    @Autowired
    private CacheProcessor cacheProcessor;

    @Override
    protected ServiceResponse<AuthenticationToken> perform(ServiceRequest<NamedValues<String>> request)
		    throws Exception {
        String username = request.getData().get(PARAM_AUTH_USERNAME);
        String password = request.getData().get(PARAM_AUTH_PASSWORD);
        String lookup = request.getData().get(PARAM_PACKAGE_LOOKUP);
        String name = request.getData().get(PARAM_SERVER_NAME);
        String cert = request.getData().get(PARAM_AUTH_CERT);
        String browserIpAddress = request.getData().get(PARAM_BROWSER_IP_ADDRESS);

        ServiceResponse<AuthenticationToken> response = null;
        boolean certAuthentication = true;
        if (StringUtils.isBlank(cert)) {
            certAuthentication = false;
            if ((StringUtils.isBlank(username) && StringUtils.isBlank(password)) ||
                    (StringUtils.isBlank(username) ^ StringUtils.isBlank(password))) {
                response = new ServiceResponse<AuthenticationToken>(
                        "Certificate or Username and Password should be provided", false);
            }
        }
        if (response == null) {
            User user = null;
            String errorMessage = "Wrong identity parameter.";
            if (certAuthentication) {
                try {
	                ServerModel serverModel = serverStore.findByPackage(lookup, name);
	                if (serverModel != null) {
		                KeyUtils.verify(cert, serverModel.getPublicKey());

		                BaseUserModel userIdentity = serverStore.findSystemUser(serverModel);
		                if (userIdentity != null) {
			                user = new User();
			                user.setId(userIdentity.getId());
			                user.setUsername(userIdentity.getUsername());
		                } else {
                            errorMessage = "Can't find System User for Server: " + serverModel.getLookup();
                        }
	                } else {
                        errorMessage = "Can't find Server entity by package lookup: " + lookup + " and server name: " + name;
                    }
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                    errorMessage = e.getMessage() + " See logs for more information";
                }
            } else {
                IUserInfoProvider provider = authenticator.authenticate(username, password);
                if (provider != null) {
                    user = new User();
                    user.setId(provider.getId());
                    user.setUsername(provider.getUsername());
                    user.setFirstName(provider.getFirstName());
                    user.setLastName(provider.getLastName());
                    user.setPassword(provider.getPassword());
                } else {
                    errorMessage = "The password you entered is incorrect. Please try again (make sure your caps lock is off).";
                }
            }

            if (user == null) {
                response = new ServiceResponse<AuthenticationToken>(errorMessage, false);
            } else {
                String sessionToken;
                UserSessionManager sessionManager = UserSessionManager.getInstance();
                if (ConfigContainer.isAppInstalled()) {
                    List<Long> roleIds = cacheProcessor.getUserRoles(user.getId());
                    sessionToken = sessionManager.openUserSession(user, cacheProcessor, roleIds, browserIpAddress);
                } else {
                    sessionToken = sessionManager.openUserSession(user);
                }
                if (certAuthentication) {
                    sessionManager.markSessionAsSystemLevel(sessionToken);
                }
                response = new ServiceResponse<AuthenticationToken>(
                        new AuthenticationToken(sessionToken, user), "Successfully authenticated.", true);
            }
        }
        return response;
    }

}