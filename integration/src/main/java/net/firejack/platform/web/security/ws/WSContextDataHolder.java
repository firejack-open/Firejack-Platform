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

package net.firejack.platform.web.security.ws;

import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;

import javax.xml.namespace.QName;

public class WSContextDataHolder {

    private static ThreadLocal<WSContext> contextHolder = new InheritableThreadLocal<WSContext>();

    public static QName AUTHENTICATION_HEADER =
            new QName("uri:net.firejack.platform",
                    OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE);

    /**
     * @return current username
     */
    public static String getCurrentUserName() {
        return getContext().getUsername();
    }

    /**
     * @return current session token
     */
    public static String getCurrentToken() {
        return getContext().getSessionToken();
    }

    /**
     * @param userName username
     * @param token session token
     */
    public static void setUserAuthenticationData(String userName, String token) {
        WSContext wsContext = getContext();
        wsContext.setUsername(userName);
        wsContext.setSessionToken(token);
    }

    public static void setClientIpAddress(String ipAddress) {
        getContext().setClientIpAddress(ipAddress);
    }

    public static String getClientIpAddress() {
        return getContext().getClientIpAddress();
    }

    public static void setRequestPath(String requestPath) {
        getContext().setRequestPath(requestPath);
    }

    public static String getRequestPath() {
        return getContext().getRequestPath();
    }

    /***/
    public static void cleanup() {
        contextHolder.remove();
    }

    private static synchronized WSContext getContext() {
        WSContext wsContext = contextHolder.get();
        if (wsContext == null) {
            wsContext = new WSContext();
            contextHolder.set(wsContext);
        }
        return wsContext;
    }
}