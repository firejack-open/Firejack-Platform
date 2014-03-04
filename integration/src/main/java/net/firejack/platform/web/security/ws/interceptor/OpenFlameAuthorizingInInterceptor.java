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

package net.firejack.platform.web.security.ws.interceptor;

import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.web.security.action.ActionDetectorFactory;
import net.firejack.platform.web.security.action.IWSActionDetector;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.security.ws.WSContextDataHolder;
import net.firejack.platform.web.security.ws.authorization.OpenFlameWSSecurityContext;
import org.apache.cxf.interceptor.security.AbstractAuthorizingInInterceptor;
import org.apache.cxf.security.SecurityContext;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class OpenFlameAuthorizingInInterceptor extends AbstractAuthorizingInInterceptor {

    private static final Logger logger = Logger.getLogger(OpenFlameAuthorizingInInterceptor.class);

    @Override
    protected List<String> getExpectedRoles(Method method) {
        return Collections.emptyList();
    }

    @Override
    protected boolean authorize(SecurityContext sc, Method method) {
        boolean allowAccess;
        OpenFlamePrincipal principal;
        if (sc instanceof OpenFlameWSSecurityContext) {
            OpenFlameWSSecurityContext securityContext = (OpenFlameWSSecurityContext) sc;
            principal = securityContext.getUserPrincipal();
        } else {
            logger.error("Specified ws securityContext should be of type " + OpenFlameWSSecurityContext.class);
            logger.warn("Set principal object to null.");
            principal = null;
        }
        if (principal == null) {
            logger.error("Failed to retrieve the principal in WS authorization interceptor. Access was restricted.");
            allowAccess = false;
        } else {
            String currentRequestPath = WSContextDataHolder.getRequestPath();
            ActionDetectorFactory actionDetectorFactory = ActionDetectorFactory.getInstance();
            IWSActionDetector actionDetector = actionDetectorFactory.produceWSActionDetector();
            logger.info("Trying to detect action for WS request ({" + currentRequestPath + "} : {" +
                    (method == null ? "null" : method.getName() + "})"));
            Action action = actionDetector.detectAction(currentRequestPath, method);
            if (action == null) {
                logger.info("Action was not detected. User unauthorized to use the web service.");
                allowAccess = false;
            } else {
                logger.info("Action [" + action.getLookup() +
                        "] was detected. Trying to check user permissions to perform the action by user [" +
                        principal.getUserInfoProvider().getUsername() + "]");
                allowAccess = principal.checkUserPermission(action);
                logger.info(allowAccess ? "Access granted." : "Access denied.");
            }
        }
        return allowAccess;
    }
}