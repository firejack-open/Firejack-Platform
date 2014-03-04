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

import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.web.security.permission.IPermissionContainer;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;


public class DefaultAuthenticationDetails implements IAuthenticationDetails {

    private List<GrantedAuthority> assignedAuthorities;
    private IAuthenticationSource authenticationSource;
    private IUserInfoProvider details;
    private IPermissionContainer permissionContainer;

    /***/
    public DefaultAuthenticationDetails() {
    }

    /**
     * @param assignedAuthorities
     * @param authenticationSource
     * @param details
     * @param permissionContainer
     */
    public DefaultAuthenticationDetails(List<GrantedAuthority> assignedAuthorities,
                                        IAuthenticationSource authenticationSource,
                                        IUserInfoProvider details,
                                        IPermissionContainer permissionContainer) {
        this.assignedAuthorities = assignedAuthorities;
        this.authenticationSource = authenticationSource;
        this.details = details;
        this.permissionContainer = permissionContainer;
    }

    /**
     * @param assignedAuthorities
     */
    public void setAssignedAuthorities(List<GrantedAuthority> assignedAuthorities) {
        this.assignedAuthorities = assignedAuthorities;
    }

    /**
     * @param authenticationSource
     */
    public void setAuthenticationSource(IAuthenticationSource authenticationSource) {
        this.authenticationSource = authenticationSource;
    }

    /**
     * @param details
     */
    public void setDetails(IUserInfoProvider details) {
        this.details = details;
    }

    @Override
    public List<GrantedAuthority> getAssignedAuthorities() {
        return assignedAuthorities;
    }

    @Override
    public IAuthenticationSource getAuthenticationSource() {
        return authenticationSource;
    }

    @Override
    public IUserInfoProvider getDetails() {
        return details;
    }

    @Override
    public IPermissionContainer getPermissionContainer() {
        return permissionContainer;
    }

}