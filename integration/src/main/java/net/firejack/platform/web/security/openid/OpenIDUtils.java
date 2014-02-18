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