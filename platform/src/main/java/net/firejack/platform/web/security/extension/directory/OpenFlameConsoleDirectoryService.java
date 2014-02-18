/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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