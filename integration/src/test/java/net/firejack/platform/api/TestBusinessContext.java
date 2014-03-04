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

package net.firejack.platform.api;


import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.attribute.CachedContextFacade;
import net.firejack.platform.web.security.model.context.ContextContainerDelegate;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.model.principal.UserPrincipal;
import net.firejack.platform.web.security.navigation.INavElementContainer;
import net.firejack.platform.web.security.navigation.INavElementContainerFactory;
import net.firejack.platform.web.security.permission.IPermissionContainer;
import net.firejack.platform.web.security.permission.IPermissionContainerFactory;
import net.firejack.platform.web.security.resource.IResourceLocationContainer;
import net.firejack.platform.web.security.resource.IResourceLocationContainerFactory;
import net.firejack.platform.web.security.sr.ISecuredRecordInfoContainer;
import net.firejack.platform.web.security.sr.ISecuredRecordInfoContainerFactory;
import net.firejack.platform.web.security.sr.ISpecifiedIdsFilterContainer;
import net.firejack.platform.web.security.sr.ISpecifiedIdsFilterContainerFactory;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class TestBusinessContext {

    static {
        OPFContext.initContextContainerDelegate(ContextContainerDelegate.getInstance());
        OPFContext.initResourceLocationContainerFactory(new IResourceLocationContainerFactory() {
            @Override
            public IResourceLocationContainer produceMaskedResourceContainer() {
                return null;
            }
        });
        OPFContext.initNavElementContainerFactory(new INavElementContainerFactory() {
            @Override
            public INavElementContainer produceNavElementContainer() {
                return null;
            }
        });
        OPFContext.initPermissionContainerFactory(new IPermissionContainerFactory() {
            @Override
            public IPermissionContainer producePermissionContainer() {
                return null;
            }
        });
        OPFContext.initSpecifiedIdsFilterContainerFactory(new ISpecifiedIdsFilterContainerFactory() {
            @Override
            public ISpecifiedIdsFilterContainer produceSpecifiedIdsFilter() {
                return null;
            }
        });
        OPFContext.initSecuredRecordContainerFactory(new ISecuredRecordInfoContainerFactory() {
            @Override
            public ISecuredRecordInfoContainer produceSecuredRecordContainer() {
                return null;
            }
        });
        ContextContainerDelegate.getInstance().initialize(null, new CachedContextFacade());
        OpenFlameSecurityConstants.setClientContext(true);
    }

    private static final Logger logger = Logger.getLogger(TestBusinessContext.class);

    public void prepareContext(String username, String password) {
        String ipAddress;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            ipAddress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            throw new BusinessFunctionException(e.getMessage(), e);
        }
        ServiceResponse<AuthenticationToken> authResp =
                OPFEngine.AuthorityService.processSTSSignIn(username, password, ipAddress);
        if (authResp.isSuccess()) {
            if (authResp.getMessage() != null) {
                logger.info("Authentication message: " + authResp.getMessage());
            }
            OpenFlamePrincipal principal = new UserPrincipal(authResp.getItem().getUser());
            OPFContext.initContext(principal, authResp.getItem().getToken());
        } else {
            logger.error("Failed to obtain authentication token. Reason: " + authResp.getMessage());
            throw new BusinessFunctionException(authResp.getMessage());
        }
    }

    public void releaseContext() {
        try {
            String sessionToken = OPFContext.getContext().getSessionToken();
            if (sessionToken != null) {
                ContextContainerDelegate.getInstance().invalidateBusinessContextInStore();
                ServiceResponse serviceResponse = OPFEngine.AuthorityService.processSTSSignOut(sessionToken);
                if (!serviceResponse.isSuccess()) {
                    throw new BusinessFunctionException(serviceResponse.getMessage());
                }
            }
        } catch (ContextLookupException e) {
            logger.error(e.getMessage(), e);
        }
    }

}