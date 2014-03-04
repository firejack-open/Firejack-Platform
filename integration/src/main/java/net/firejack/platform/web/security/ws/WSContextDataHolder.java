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