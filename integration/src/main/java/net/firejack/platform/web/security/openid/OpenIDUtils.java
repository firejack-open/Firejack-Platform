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

package net.firejack.platform.web.security.openid;

import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenIDUtils {

    private static final Logger logger = Logger.getLogger(OpenIDUtils.class);

    /**
     * @param email
     * @param attributeValues
     * @param browserIpAddress
     * @return
     */
    public static Map<String, String> prepareOpenIDUserHttpHeaders(
            String email, Map<SupportedOpenIDAttribute, String> attributeValues, String browserIpAddress) {
        Map<String, String> headers = new HashMap<String, String>();
        for (SupportedOpenIDAttribute key : attributeValues.keySet()) {
            headers.put(OpenFlameSecurityConstants.OPENID_HEADER_PREFIX + key.getAttributeName(), attributeValues.get(key));
        }
        headers.put("email", email);
        headers.put("browser.ip.address", email);
        return headers;
    }

    /**
     * @param httpHeaders
     * @return
     */
    public static Map<SupportedOpenIDAttribute, String> prepareOpenIDUserAttributes(
            MultivaluedMap<String, String> httpHeaders) {
        Map<SupportedOpenIDAttribute, String> openIDHeaders = new HashMap<SupportedOpenIDAttribute, String>();
        for (String headerName : httpHeaders.keySet()) {
            if (headerName.startsWith(OpenFlameSecurityConstants.OPENID_HEADER_PREFIX)) {
                String openIDAttributeName = headerName.substring(OpenFlameSecurityConstants.OPENID_HEADER_PREFIX.length());
                SupportedOpenIDAttribute supportedOpenIDAttribute =
                        SupportedOpenIDAttribute.lookForSupportedAttribute(openIDAttributeName);
                if (supportedOpenIDAttribute == null) {
                    logger.warn("Failed to find openId attribute info using [" + openIDAttributeName + "]");
                } else {
                    openIDHeaders.put(supportedOpenIDAttribute, httpHeaders.getFirst(headerName));
                }
            }
        }
        return openIDHeaders;
    }

    /**
     * @param httpHeaders
     * @return
     */
    public static Map<SupportedOpenIDAttribute, String> prepareOpenIDUserAttributes(
            Map<String, List<String>> httpHeaders) {
        Map<SupportedOpenIDAttribute, String> openIDHeaders = new HashMap<SupportedOpenIDAttribute, String>();
        for (String headerName : httpHeaders.keySet()) {
            if (headerName.startsWith(OpenFlameSecurityConstants.OPENID_HEADER_PREFIX)) {
                String openIDAttributeName = headerName.substring(OpenFlameSecurityConstants.OPENID_HEADER_PREFIX.length());
                SupportedOpenIDAttribute supportedOpenIDAttribute =
                        SupportedOpenIDAttribute.lookForSupportedAttribute(openIDAttributeName);
                if (supportedOpenIDAttribute == null) {
                    logger.warn("Failed to find openId attribute info using [" + openIDAttributeName + "]");
                } else {
                    openIDHeaders.put(supportedOpenIDAttribute, httpHeaders.get(headerName).get(0));
                }
            }
        }
        return openIDHeaders;
    }
}