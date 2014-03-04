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

package net.firejack.platform.web.security.extension.directory;

import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.registry.IDirectoryStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.service.authority.utils.ISecurityAuthenticator;
import net.firejack.platform.web.security.directory.IDirectoryService;
import net.firejack.platform.web.security.directory.annotation.DirectoryServiceConfig;
import net.firejack.platform.web.security.directory.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

@DirectoryServiceConfig(
        lookup = OpenFlame.SIGN_UP_DIRECTORY,
        title = "Firejack Platform Console Directory Service.",
        defaults = true
)
public class OpenFlameConsoleDirectoryService implements IDirectoryService {

    private ISecurityAuthenticator securityAuthenticator;
    private IUserStore userStore;
    private IDirectoryStore directoryStore;
    private IRoleStore roleStore;
    private IUserRoleStore userRoleStore;
    private String defaultDirectoryLookup;
    private String defaultRoleLookup;

    /**
     * @param defaultDirectoryLookup
     */
    @Required
    public void setDefaultDirectoryLookup(String defaultDirectoryLookup) {
        this.defaultDirectoryLookup = defaultDirectoryLookup;
    }

    /**
     * @param defaultRoleLookup
     */
    @Required
    public void setDefaultRoleLookup(String defaultRoleLookup) {
        this.defaultRoleLookup = defaultRoleLookup;
    }

    /**
     * @param securityAuthenticator
     */
    @Required
    public void setSecurityAuthenticator(ISecurityAuthenticator securityAuthenticator) {
        this.securityAuthenticator = securityAuthenticator;
    }

    /**
     * @param userStore
     */
    @Required
    public void setUserStore(IUserStore userStore) {
        this.userStore = userStore;
    }

    /**
     * @param directoryStore
     */
    @Required
    public void setDirectoryStore(IDirectoryStore directoryStore) {
        this.directoryStore = directoryStore;
    }

    /**
     * @param roleStore
     */
    @Required
    public void setRoleStore(IRoleStore roleStore) {
        this.roleStore = roleStore;
    }

    /**
     * @param userRoleStore
     */
    @Required
    public void setUserRoleStore(IUserRoleStore userRoleStore) {
        this.userRoleStore = userRoleStore;
    }

    @Override
    public IUserInfoProvider authenticate(String userName, String password) {
        return securityAuthenticator.authenticate(userName, password);
    }

    @Override
    public IUserInfoProvider createUser(String userName, String password, String email,
                                        Map<String, String> additionalProperties) throws UserAlreadyExistsException {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password) || StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Username, password or email should not be blank.");
        }
        UserModel user = userStore.findUserByUsername(userName);
        if (user != null) {
            throw new UserAlreadyExistsException("User with specified username should not be blank.");
        }
        user = userStore.findUserByEmail(email);
        if (user != null) {
            throw new UserAlreadyExistsException("User with specified email should not be blank.");
        }
        DirectoryModel defaultDirectory = directoryStore.findByLookup(defaultDirectoryLookup);
        if (defaultDirectory == null) {
            throw new IllegalStateException("Failed to load default directory by configured lookup [" + defaultDirectoryLookup + "].");
        }
        RoleModel defaultRole = roleStore.findByLookup(defaultRoleLookup);
        if (defaultRole == null) {
            throw new IllegalStateException("Failed to load default role by configured lookup [" + defaultRoleLookup + "].");
        }
        user = new UserModel();
        user.setUsername(userName);
        user.setPassword(password);
        user.setEmail(email);
        user.setFirstName(additionalProperties.get("firstName"));
        user.setLastName(additionalProperties.get("lastName"));
        user.setMiddleName(additionalProperties.get("middleName"));
        user.setRegistryNode(defaultDirectory);
        userStore.saveOrUpdate(user);

        UserRoleModel userRole = new UserRoleModel(user, defaultRole);
        userRoleStore.saveOrUpdate(userRole);

        return user;
    }
}