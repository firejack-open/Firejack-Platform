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