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

package net.firejack.platform.web.security.spring.openid;

import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;
import org.openid4java.consumer.ConsumerException;
import org.springframework.security.openid.OpenID4JavaConsumer;
import org.springframework.security.openid.OpenIDAttribute;

import java.util.Arrays;


public class AttributeAwareOpenIDConsumer extends OpenID4JavaConsumer {

    public static final OpenIDAttribute USERNAME = toOpenIDAttribute(SupportedOpenIDAttribute.USERNAME);
    public static final OpenIDAttribute EMAIL = toOpenIDAttribute(SupportedOpenIDAttribute.EMAIL);
    public static final OpenIDAttribute FIRST_NAME = toOpenIDAttribute(SupportedOpenIDAttribute.FIRST_NAME);
    public static final OpenIDAttribute MIDDLE_NAME = toOpenIDAttribute(SupportedOpenIDAttribute.MIDDLE_NAME);
    public static final OpenIDAttribute LAST_NAME = toOpenIDAttribute(SupportedOpenIDAttribute.LAST_NAME);

    /**
     * @throws org.openid4java.consumer.ConsumerException
     *
     */
    public AttributeAwareOpenIDConsumer() throws ConsumerException {
        super(Arrays.asList(USERNAME, EMAIL, FIRST_NAME, LAST_NAME, MIDDLE_NAME));
    }

    /**
     * @param supportedAttribute
     * @return
     */
    public static OpenIDAttribute toOpenIDAttribute(SupportedOpenIDAttribute supportedAttribute) {
        OpenIDAttribute openIDAttribute = new OpenIDAttribute(
                supportedAttribute.getAttributeName(), supportedAttribute.getAttributeType());
        openIDAttribute.setRequired(true);
        return openIDAttribute;
    }
}
