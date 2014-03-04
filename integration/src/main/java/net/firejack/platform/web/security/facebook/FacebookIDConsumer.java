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
