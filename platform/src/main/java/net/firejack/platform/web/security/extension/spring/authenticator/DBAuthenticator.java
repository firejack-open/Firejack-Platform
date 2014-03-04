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

package net.firejack.platform.web.security.extension.spring.authenticator;

import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.web.security.cridential.IPasswordEncryptionStrategy;
import net.firejack.platform.web.security.permission.IPermissionContainer;
import net.firejack.platform.web.security.permission.IPermissionContainerFactory;
import net.firejack.platform.web.security.spring.SpringSecurityAuthenticationRoleType;
import net.firejack.platform.web.security.spring.authenticator.Authenticator;
import net.firejack.platform.web.security.spring.authenticator.DefaultAuthenticationDetails;
import net.firejack.platform.web.security.spring.authenticator.IAuthenticationDetails;
import net.firejack.platform.web.security.spring.authenticator.IAuthenticationSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;


public abstract class DBAuthenticator extends Authenticator {

    @Autowired
    @Qualifier("passwordEncryptionStrategy")
    public IPasswordEncryptionStrategy passwordEncryptionStrategy;

    private IPermissionContainerFactory permissionContainerFactory;

    /**
     * @param permissionContainerFactory
     */
    public void setPermissionContainerFactory(IPermissionContainerFactory permissionContainerFactory) {
        this.permissionContainerFactory = permissionContainerFactory;
    }

    @Override
    public IAuthenticationDetails authenticate(IAuthenticationSource authenticationSource) {
        String userName = authenticationSource.getPrincipal();
        String password = authenticationSource.getCredential();

        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            String encryptedPassword = passwordEncryptionStrategy.encryptPassword(password);

            UserModel user = findUserByUsernameAndPassword(userName, encryptedPassword);
            if (user != null) {
                List<SpringSecurityAuthenticationRoleType> roles = new ArrayList<SpringSecurityAuthenticationRoleType>();
                roles.add(SpringSecurityAuthenticationRoleType.ROLE_USER); //todo: eliminate such usages
                IPermissionContainer permissionContainer = permissionContainerFactory.producePermissionContainer();
                return new DefaultAuthenticationDetails(provideGrantedAuthorities(roles), authenticationSource, user, permissionContainer);
            }
        }
        return null;
    }

    /**
     * @param username
     * @param password
     * @return
     */
    public abstract UserModel findUserByUsernameAndPassword(String username, String password);

}