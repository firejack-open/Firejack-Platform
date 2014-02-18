/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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