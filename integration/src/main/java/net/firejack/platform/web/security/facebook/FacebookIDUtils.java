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

package net.firejack.platform.web.security.facebook;

import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.Map;

public class FacebookIDUtils {

    /**
     * @param httpHeaders
     * @return
     */
    public static Map<String, String> prepareFacebookIDUserAttributes(MultivaluedMap<String, String> httpHeaders) {
        Map<String, String> facebookIDHeaders = new HashMap<String, String>();
        for (String headerName : httpHeaders.keySet()) {
            if (headerName.startsWith(OpenFlameSecurityConstants.FACEBOOK_HEADER_PREFIX)) {
                String openIDAttributeName = headerName.substring(OpenFlameSecurityConstants.FACEBOOK_HEADER_PREFIX.length());
                facebookIDHeaders.put(openIDAttributeName, httpHeaders.getFirst(headerName));
            }
        }
        return facebookIDHeaders;
    }

    public static Map<String, String> prepareFacebookIDUserHttpHeaders(Long facebookId, Map<String, String> attributeValues, String browserIpAddress) {
        Map<String, String> headers = new HashMap<String, String>();
        for (String name : attributeValues.keySet()) {
            headers.put(OpenFlameSecurityConstants.FACEBOOK_HEADER_PREFIX + name, attributeValues.get(name));
        }
        headers.put("facebookId", facebookId.toString());
        headers.put(OpenFlameSecurityConstants.IP_ADDRESS_HEADER, browserIpAddress);
        return headers;
    }

}