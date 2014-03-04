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

import net.firejack.platform.web.security.cridential.IPasswordEncryptionStrategy;
import net.firejack.platform.web.security.spring.SpringSecurityAuthenticationRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.util.ArrayList;
import java.util.List;


public abstract class Authenticator implements IAuthenticator {

    @Autowired
    @Qualifier("passwordEncryptionStrategy")
    public IPasswordEncryptionStrategy passwordEncryptionStrategy;

    public abstract IAuthenticationDetails authenticate(IAuthenticationSource authenticationSource);

    /**
     * @param roles
     * @return
     */
    public List<GrantedAuthority> provideGrantedAuthorities(List<SpringSecurityAuthenticationRoleType> roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (SpringSecurityAuthenticationRoleType role : roles) {
            authorities.add(new GrantedAuthorityImpl(role.name()));
        }
        return authorities;
    }

}