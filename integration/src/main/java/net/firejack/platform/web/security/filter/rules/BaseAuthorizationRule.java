/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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