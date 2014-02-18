/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.service.authority.utils;

import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.security.openid.SupportedOpenIDAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpenIDProcessor {

    @Autowired
    private IUserStore userStore;

    @Autowired
    private IRoleStore roleStore;

    @Autowired
    @Qualifier("directoryStore")
    private IRegistryNodeStore<DirectoryModel> directoryStore;

    /**
     * @param email
     * @return
     */
    public UserModel findUserByEmail(String email) {
        return userStore.findUserByEmail(email);
    }

    /**
     * @param values
     * @return
     */
    public Tuple<UserModel, List<RoleModel>> createUserFromOpenIDAttributes(Map<SupportedOpenIDAttribute, String> values) {
        String username = values.get(SupportedOpenIDAttribute.USERNAME);
        String email = values.get(SupportedOpenIDAttribute.EMAIL);
        String firstName = values.get(SupportedOpenIDAttribute.FIRST_NAME);
        String lastName = values.get(SupportedOpenIDAttribute.LAST_NAME);
        String middleName = values.get(SupportedOpenIDAttribute.MIDDLE_NAME);

        if (StringUtils.isBlank(username)) {
            if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
                username = (firstName + lastName).toLowerCase();
            } else {
                username = email.substring(0, email.indexOf("@"));
            }
        }
        int usernameSuffix = 1;
        boolean isUsernameUnique = false;
        while (!isUsernameUnique) {
            String checkUsername = username;
            if (usernameSuffix > 1) {
                checkUsername += usernameSuffix;
            }
            UserModel user = userStore.findUserByUsername(checkUsername);
            if (user == null) {
                username = checkUsername;
                isUsernameUnique = true;
            } else {
                usernameSuffix++;
            }
        }

        UserModel newUser = new UserModel();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setMiddleName(middleName);

        List<RoleModel> roles = roleStore.findByName(OpenFlame.USER_ROLE_NAME);
        List<UserRoleModel> userRoles = new ArrayList<UserRoleModel>();
        for (RoleModel role : roles) {
            UserRoleModel userRole = new UserRoleModel(newUser, role);
            userRoles.add(userRole);
        }
        newUser.setUserRoles(userRoles);

        DirectoryModel directory = directoryStore.findByLookup(OpenFlame.SIGN_UP_DIRECTORY);
        newUser.setRegistryNode(directory);

        userStore.save(newUser);
        return new Tuple<UserModel, List<RoleModel>>(newUser, roles);

    }
}