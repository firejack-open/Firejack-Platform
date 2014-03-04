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

package net.firejack.platform.web.security.spring.authenticator;

import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;
import net.firejack.platform.web.security.spring.openid.OpenIDAuthenticationSource;
import org.springframework.security.core.Authentication;

import java.util.Map;

public final class AuthenticatorFactory {

    private static AuthenticatorFactory instance;

    /**
     * @return
     */
    public static AuthenticatorFactory getInstance() {
        if (instance == null) {
            instance = new AuthenticatorFactory();
        }
        return instance;
    }

    /**
     * @param authentication
     * @return
     */
    public IAuthenticationSource provideDefaultAuthenticationSource(Authentication authentication) {
        return new DefaultAuthenticationSource(authentication);
    }

    /**
     * @param userName
     * @param password
     * @return
     */
    public IAuthenticationSource provideDefaultAuthenticationSource(String userName, String password) {
        return new DefaultAuthenticationSource(userName, password);
    }

    /**
     * @param email
     * @param attibutes
     * @return
     */
    public IAuthenticationSource provideOpenIDAuthenticationSource(String email, Map<SupportedOpenIDAttribute, String> attibutes) {
        return new OpenIDAuthenticationSource(email, attibutes);
    }

}