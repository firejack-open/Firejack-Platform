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

package net.firejack.platform.web.security.filter.rules;

import net.firejack.platform.api.authority.domain.SecurityDriven;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.web.security.filter.ISecurityFilter;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseAuthorizationRule<T extends SecurityDriven> implements IAuthorizationRule {

    private ThreadLocal<T> elementHolder = new InheritableThreadLocal<T>();
    private static final Logger logger = Logger.getLogger(BaseAuthorizationRule.class);

    @Override
    public boolean isRuleCase(ISecurityFilter securityFilter, HttpServletRequest httpRequest, EntityProtocol protocol) {
        T element = detectElement(securityFilter, httpRequest, protocol);
        if (element != null) {
            onElementDetected(securityFilter, httpRequest, protocol, element);
            return true;
        }
        return false;
    }

    @Override
    public Boolean authorizeAccess(ISecurityFilter securityFilter, HttpServletRequest httpRequest,
                                   HttpServletResponse httpResponse) throws IOException {
        T element = getElement();
        if (element == null) {
            throw new IllegalStateException();
        }
        boolean result = true;
        OPFContext context = OPFContext.getContext();
        OpenFlamePrincipal principal = context.getPrincipal();
        if (principal.checkUserPermission(element)) {
            onSuccessfulAuthorization(element);
        } else {
            onFailureAuthorization(element, securityFilter, httpRequest, httpResponse);
            result = false;
        }
        return result;
    }

    @Override
    public void initialize() {
        //
    }

    @Override
    public void release() {
        if (elementHolder.get() != null) {
            elementHolder.remove();
        }
    }

    protected void putElement(T element) {
        elementHolder.set(element);
    }

    protected T getElement() {
        return elementHolder.get();
    }

    @SuppressWarnings("unused")
    protected void onElementDetected(ISecurityFilter securityFilter, HttpServletRequest httpRequest, EntityProtocol protocol, T detectedElement) {
        logger.info("Secured element detected for context: [" + protocol + " : " + httpRequest.getRequestURL());
        putElement(detectedElement);
    }

    protected void onSuccessfulAuthorization(T element) {
        logger.info("User [" + ContextManager.getCurrentUserName() +
                "] authorized to access secured element mapped to url [" + element.getUrlPath() + "].");
    }

    protected void onFailureAuthorization(
            T element, ISecurityFilter securityFilter, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws IOException {
        if (ContextManager.isUserAuthenticated()) {
            onUserUnauthorized(element, securityFilter, httpRequest, httpResponse);
        } else {
            onGuestUnauthorized(element, securityFilter, httpRequest, httpResponse);
        }
    }

    protected void onGuestUnauthorized(
            T element, ISecurityFilter securityFilter,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        logger.info("Guest user tries to access secured element [" +
                element.getLookup() + "]. Redirecting to sign-in page...");
        securityFilter.showSignInPage(httpRequest, httpResponse);
    }

    protected void onUserUnauthorized(
            T element, ISecurityFilter securityFilter,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        logger.info("Access denied for user [" +
                ContextManager.getCurrentUserName() + "], attempt to access secured element[" +
                element.getLookup() + "] mapped to url [" + element.getUrlPath() +
                "]. Redirecting to sign-in page...");
        securityFilter.showAccessDeniedPage(httpRequest, httpResponse);
    }

    protected abstract T detectElement(ISecurityFilter securityFilter, HttpServletRequest httpRequest, EntityProtocol protocol);

}