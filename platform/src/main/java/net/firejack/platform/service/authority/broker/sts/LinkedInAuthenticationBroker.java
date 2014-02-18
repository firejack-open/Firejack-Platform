/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.service.authority.broker.sts;

import net.firejack.platform.api.authority.domain.AuthenticationToken;
import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.cache.CacheProcessor;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.security.session.UserSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class LinkedInAuthenticationBroker extends ServiceBroker<ServiceRequest<NamedValues<Object>>, ServiceResponse<AuthenticationToken>> {

    public static final String PARAM_LINKEDIN_ATTRIBUTES = "linkedinIdAttributes";
    public static final String PARAM_BROWSER_IP_ADDRESS = "browserIpAddress";

    @Autowired
    @Qualifier("cacheProcessor")
    private CacheProcessor cacheProcessor;

    @Autowired
    private IUserStore userStore;

    @Autowired
    private IRoleStore roleStore;

    @Autowired
    @Qualifier("directoryStore")
    private IRegistryNodeStore<DirectoryModel> directoryStore;

    @Override
    protected ServiceResponse<AuthenticationToken> perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        String browserIpAddress = (String) request.getData().get(PARAM_BROWSER_IP_ADDRESS);
        Map<String, String> map = (Map<String, String>) request.getData().get(PARAM_LINKEDIN_ATTRIBUTES);
        @SuppressWarnings("unchecked")
        Tuple<UserModel, List<RoleModel>> tuple = processLinkedInUser(map);
        UserModel userModel = tuple.getKey();
        List<RoleModel> userRoles = tuple.getValue();

        List<Long> roleIds = new ArrayList<Long>();
        List<Role> roles = new ArrayList<Role>();
        if (userRoles != null) {
            for (RoleModel roleModel : userRoles) {
                Role role = new Role();
                role.setId(roleModel.getId());
                role.setName(roleModel.getName());
                role.setPath(roleModel.getPath());
                role.setLookup(roleModel.getLookup());
                role.setParentId(roleModel.getParent().getId());

                roleIds.add(roleModel.getId());
                roles.add(role);
            }
        }
        UserSessionManager userSessionManager = UserSessionManager.getInstance();
        String token = userSessionManager.openUserSession(userModel, cacheProcessor, roleIds, browserIpAddress);

        User user = factory.convertTo(User.class, userModel);
        user.setRoles(roles);

        if (user.getUserRoles() != null) {
            for (UserRole userRole : user.getUserRoles()) {
                userRole.setUser(null);
            }
        }

        AuthenticationToken authenticationToken = new AuthenticationToken(token, user);
        return new ServiceResponse<AuthenticationToken>(authenticationToken, "LinkedIn user authenticated successfully", true);
    }

    private Tuple<UserModel, List<RoleModel>> processLinkedInUser(Map<String, String> mappedAttributes) {
        String linkedInId = mappedAttributes.get("id");
        UserModel userModel = userStore.findUserByLinkedInId(linkedInId);
        List<RoleModel> roleModels;
        if (userModel == null) {
            String email = mappedAttributes.get("email-address");
            userModel = userStore.findUserByEmail(email);
            if (userModel == null) {
                String firstName = mappedAttributes.get("first-name");
                String lastName = mappedAttributes.get("last-name");

                String username = (firstName + lastName).toLowerCase();
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
                newUser.setLinkedInId(linkedInId);

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

                userModel = newUser;
                roleModels = roles;
            } else {
                userModel.setLinkedInId(linkedInId);
                userStore.saveOrUpdate(userModel);
                roleModels = roleStore.findRolesByUserId(userModel.getId());
            }
        } else {
            roleModels = roleStore.findRolesByUserId(userModel.getId());
        }
        return new Tuple<UserModel, List<RoleModel>>(userModel, roleModels);
    }
}
