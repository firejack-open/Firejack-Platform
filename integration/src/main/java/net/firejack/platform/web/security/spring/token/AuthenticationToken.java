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

package net.firejack.platform.web.security.spring.token;

import net.firejack.platform.web.security.permission.IPermissionContainer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpSession;
import java.util.Collection;


public class AuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = -8074055316495929620L;

    private HttpSession session;
    private IPermissionContainer permissionContainer;

    /**
     * @param principal
     * @param credentials
     */
    public AuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    /**
     * @param principal
     * @param credentials
     * @param authorities
     * @param permissionContainer
     */
    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, IPermissionContainer permissionContainer) {
        super(principal, credentials, authorities);
        this.permissionContainer = permissionContainer;
    }


    /**
     * @return
     */
    public HttpSession getSession() {
        return session;
    }

    /**
     * @param session
     */
    public void setSession(HttpSession session) {
        this.session = session;
    }

    /**
     * @return
     */
    public IPermissionContainer getPermissionHolder() {
        return permissionContainer;
    }

    /**
     * @param permissionContainer
     */
    public void setPermissionHolder(IPermissionContainer permissionContainer) {
        this.permissionContainer = permissionContainer;
    }

}
