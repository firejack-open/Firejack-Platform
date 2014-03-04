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
import net.firejack.platform.web.security.spring.authenticator.IAuthenticationSource;

import java.util.Map;


public class OpenIDAuthenticationSource implements IAuthenticationSource {

    private String email;
    private Map<SupportedOpenIDAttribute, String> attributes;

    /**
     * @param email
     * @param attributes
     */
    public OpenIDAuthenticationSource(String email, Map<SupportedOpenIDAttribute, String> attributes) {
        this.email = email;
        this.attributes = attributes;
    }

    @Override
    public String getPrincipal() {
        return email;
    }

    @Override
    public String getCredential() {
        return null;
    }

    /**
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return
     */
    public Map<SupportedOpenIDAttribute, String> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes
     */
    public void setAttributes(Map<SupportedOpenIDAttribute, String> attributes) {
        this.attributes = attributes;
    }

}