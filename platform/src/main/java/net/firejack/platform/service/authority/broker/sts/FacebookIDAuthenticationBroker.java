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
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.service.authority.utils.FacebookIDProcessor;
import net.firejack.platform.web.security.session.UserSessionManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@TrackDetails
@Component("facebookIDAuthenticationBrokerEx")
public class FacebookIDAuthenticationBroker
        extends ServiceBroker<ServiceRequest<NamedValues<Object>>, ServiceResponse<AuthenticationToken>> {

    public static final String PARAM_FACEBOOK_ID = "facebook_id";
    public static final String PARAM_FACEBOOK_ID_ATTRIBUTES = "facebookIdAttributes";
    public static final String PARAM_BROWSER_IP_ADDRESS = "browserIpAddress";
    @Autowired
    @Qualifier("cacheProcessor")
    private CacheProcessor cacheProcessor;

    @Autowired
    private FacebookIDProcessor facebookIDProcessor;
    @Autowired
    private IRoleStore roleStore;

    @Override
    protected ServiceResponse<AuthenticationToken> perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        Long facebookId = (Long) request.getData().get(PARAM_FACEBOOK_ID);
        String browserIpAddress = (String) request.getData().get(PARAM_BROWSER_IP_ADDRESS);
        UserModel userModel = facebookIDProcessor.findUserByFacebookId(facebookId);
        List<RoleModel> userRoles;
        if (userModel == null) {
            @SuppressWarnings("unchecked")
            Map<String, String> mappedAttributes = (Map<String, String>) request.getData().get(PARAM_FACEBOOK_ID_ATTRIBUTES);
            Tuple<UserModel, List<RoleModel>> userRolesTuple = facebookIDProcessor.createUserFromFacebookIDAttributes(facebookId, mappedAttributes);
            userModel = userRolesTuple.getKey();
            userRoles = userRolesTuple.getValue();
        } else {
            userRoles = roleStore.findRolesByUserId(userModel.getId());
        }
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
        return new ServiceResponse<AuthenticationToken>(authenticationToken, "Facebook user authenticated successfully", true);
    }
}