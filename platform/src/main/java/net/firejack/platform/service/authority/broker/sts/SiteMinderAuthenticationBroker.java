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
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.security.session.UserSessionManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 *
 */
@TrackDetails
@Component
public class SiteMinderAuthenticationBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<AuthenticationToken>> {

    public static final String PARAM_USER = "user";
    public static final String PARAM_BROWSER_IP_ADDRESS = "ipAddress";

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
    protected ServiceResponse<AuthenticationToken> perform(ServiceRequest<NamedValues<Object>> request)
            throws Exception {
        String browserIpAddress = (String) request.getData().get(PARAM_BROWSER_IP_ADDRESS);
        User user = (User) request.getData().get(PARAM_USER);
        Tuple<UserModel, List<RoleModel>> tuple = processSiteMinderUser(user);

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

        user = factory.convertTo(User.class, userModel);
        user.setRoles(roles);

        if (user.getUserRoles() != null) {
            for (UserRole userRole : user.getUserRoles()) {
                userRole.setUser(null);
            }
        }
        userSessionManager.markSessionAsSiteMinderLevel(token);
        AuthenticationToken authenticationToken = new AuthenticationToken(token, user);
        return new ServiceResponse<AuthenticationToken>(
                authenticationToken, "SiteMinder user authenticated successfully", true);
    }

    private Tuple<UserModel, List<RoleModel>> processSiteMinderUser(User user) {
        UserModel userModel = userStore.findUserByUsername(user.getUsername());
        List<RoleModel> roleModels;
        if (userModel == null) {
            UserModel newUser = new UserModel();
            newUser.setUsername(user.getUsername());
            String email;
            if (StringUtils.isBlank(user.getEmail())) {
                email = getDefaultEmail(user.getUsername());
            } else {
                UserModel userByEmail = userStore.findUserByEmail(user.getEmail());
                email = userByEmail == null ? user.getEmail() : getDefaultEmail(user.getUsername());
            }
            newUser.setEmail(email);
            newUser.setFirstName(StringUtils.isBlank(user.getFirstName()) ? user.getUsername() : user.getFirstName());
            newUser.setLastName(StringUtils.isBlank(user.getLastName()) ? user.getUsername() : user.getLastName());

            List<RoleModel> roles;
            if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
                roles = new ArrayList<RoleModel>();
            } else {
                List<String> roleLookupList = new ArrayList<String>();
                for (UserRole userRole : user.getUserRoles()) {
                    Role role = userRole.getRole();
                    if (role != null && !StringUtils.isBlank(role.getLookup())) {
                        roleLookupList.add(role.getLookup());
                    }
                }
                if (roleLookupList.isEmpty()) {
                    roles = new ArrayList<RoleModel>();
                } else {
                    LinkedList<Criterion> criterionList = new LinkedList<Criterion>();
                    criterionList.add(Restrictions.in("lookup", roleLookupList));
                    roles = roleStore.search(criterionList, null);
                }
            }

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
            roleModels = roleStore.findRolesByUserId(userModel.getId());
        }
        return new Tuple<UserModel, List<RoleModel>>(userModel, roleModels);
    }

    private String getDefaultEmail(String standardId) {
        return standardId + "@change.me";
    }
}