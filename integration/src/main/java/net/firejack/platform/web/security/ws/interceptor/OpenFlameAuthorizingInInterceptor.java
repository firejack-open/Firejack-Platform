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