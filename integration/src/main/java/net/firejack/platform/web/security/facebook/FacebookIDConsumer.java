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

import com.google.code.facebookapi.ProfileField;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.core.response.ServiceResponse;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class FacebookIDConsumer {

    private static final Logger logger = Logger.getLogger(FacebookIDConsumer.class);

    public AuthenticationToken processResponse(Long facebookUserID, Document document, String ipAddress) {
        Map<String, String> mappedAttributes = new HashMap<String, String>();
        mappedAttributes.put(ProfileField.NAME.fieldName(), getValue(document, ProfileField.NAME.fieldName()));
        mappedAttributes.put(ProfileField.FIRST_NAME.fieldName(), getValue(document, ProfileField.FIRST_NAME.fieldName()));
        mappedAttributes.put(ProfileField.LAST_NAME.fieldName(), getValue(document, ProfileField.LAST_NAME.fieldName()));
        mappedAttributes.put(ProfileField.ABOUT_ME.fieldName(), getValue(document, ProfileField.ABOUT_ME.fieldName()));
        mappedAttributes.put(ProfileField.CURRENT_LOCATION.fieldName(), getValue(document, ProfileField.CURRENT_LOCATION.fieldName()));
        mappedAttributes.put(ProfileField.LOCALE.fieldName(), getValue(document, ProfileField.LOCALE.fieldName()));
        mappedAttributes.put(ProfileField.PIC_SQUARE.fieldName(), getValue(document, ProfileField.PIC_SQUARE.fieldName()));
        mappedAttributes.put(ProfileField.PIC_BIG.fieldName(), getValue(document, ProfileField.PIC_BIG.fieldName()));
        ServiceResponse<AuthenticationToken> response = OPFEngine.AuthorityService.processFacebookIdSignIn(facebookUserID, mappedAttributes, ipAddress);
        AuthenticationToken token;
        if (response == null) {
            throw new IllegalStateException("API Service response should not be null.");
        } else if (response.isSuccess()) {
            token = response.getItem();
        } else {
            logger.error("API Service response has failure status. Reason: " + response.getMessage());
            token = null;
        }
        return token;
    }

    private String getValue(Document document, String name) {
        String value = null;
        NodeList nodeList = document.getElementsByTagName(name);
        if (nodeList != null) {
            Node node = nodeList.item(0);
            if (node != null) {
                value = node.getTextContent();
            }
        }
        return value;
    }
}
