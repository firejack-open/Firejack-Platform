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

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.web.security.cridential.IPasswordEncryptionStrategy;
import net.firejack.platform.web.security.permission.PermissionContainer;
import net.firejack.platform.web.security.spring.SpringSecurityAuthenticationRoleType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;


public class ConfigurableAuthenticator extends Authenticator {

    @Autowired
    @Qualifier("passwordEncryptionStrategy")
    public IPasswordEncryptionStrategy passwordEncryptionStrategy;

    private IUserInfoProvider user;
    private List<SpringSecurityAuthenticationRoleType> roles;
    private List<UserPermission> grantedPermissions;
    private List<UserPermission> deniedPermissions;

    /**
     * @param user
     */
    public void setUser(IUserInfoProvider user) {
        this.user = user;
    }

    /**
     * @param roles
     */
    public void setRoles(List<SpringSecurityAuthenticationRoleType> roles) {
        this.roles = roles;
    }

    /**
     * @param grantedPermissions
     */
    public void setGrantedPermissions(List<UserPermission> grantedPermissions) {
        this.grantedPermissions = grantedPermissions;
    }

    /**
     * @param deniedPermissions
     */
    public void setDeniedPermissions(List<UserPermission> deniedPermissions) {
        this.deniedPermissions = deniedPermissions;
    }

    /**
     * @return
     */
    public List<SpringSecurityAuthenticationRoleType> getRoles() {
        if (roles == null) {
            roles = new ArrayList<SpringSecurityAuthenticationRoleType>();
            roles.add(SpringSecurityAuthenticationRoleType.ROLE_DEFAULT); //todo: eliminate such usages
        }
        return roles;
    }

    @Override
    public IAuthenticationDetails authenticate(IAuthenticationSource authenticationSource) {
        String userName = authenticationSource.getPrincipal();
        String password = authenticationSource.getCredential();
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            password = passwordEncryptionStrategy.encryptPassword(password);
            if (user != null && user.getUsername().equals(userName) && user.getPassword().equals(password)) {
                PermissionContainer permissionLoader = new PermissionContainer();
                permissionLoader.setGrantedActions(grantedPermissions);
                permissionLoader.setDeniedActions(deniedPermissions);
                return new DefaultAuthenticationDetails(provideGrantedAuthorities(getRoles()), authenticationSource, user, permissionLoader);
            }
        }
        return null;
    }

}
