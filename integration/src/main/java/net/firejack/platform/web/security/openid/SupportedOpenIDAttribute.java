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

import net.firejack.platform.core.utils.StringUtils;
import org.openid4java.message.MessageException;
import org.openid4java.message.ax.FetchRequest;

public enum SupportedOpenIDAttribute {

    USERNAME("nickname", "http://axschema.org/namePerson/friendly"),
    EMAIL("email", "http://axschema.org/contact/email"),
    FIRST_NAME("firstName", "http://axschema.org/namePerson/first"),
    MIDDLE_NAME("middleName", "http://axschema.org/namePerson/middle"),
    LAST_NAME("lastName", "http://axschema.org/namePerson/last");

    private String attributeName;
    private String attributeType;

    SupportedOpenIDAttribute(String attributeName, String attributeType) {
        this.attributeName = attributeName;
        this.attributeType = attributeType;
    }

    /**
     * @return
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @return
     */
    public String getAttributeType() {
        return attributeType;
    }

    /**
     * @param fetchRequest
     * @throws org.openid4java.message.MessageException
     *
     */
    public void append(FetchRequest fetchRequest) throws MessageException {
        fetchRequest.addAttribute(getAttributeName(), getAttributeType(), true);
    }

    /**
     * @param attributeName
     * @return
     */
    public static SupportedOpenIDAttribute lookForSupportedAttribute(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            return null;
        }

        SupportedOpenIDAttribute[] openIDAttributes = values();
        for (SupportedOpenIDAttribute attribute : openIDAttributes) {
            if (attribute.getAttributeName().equals(attributeName)) {
                return attribute;
            }
        }
        return null;
    }
}