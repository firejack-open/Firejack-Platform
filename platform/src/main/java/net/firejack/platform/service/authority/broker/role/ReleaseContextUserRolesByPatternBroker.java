/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.service.authority.broker.role;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.BaseUser;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Component
@TrackDetails
public class ReleaseContextUserRolesByPatternBroker extends ServiceBroker<ServiceRequest<UserRole>, ServiceResponse> {

    @Autowired
    private IUserRoleStore userRoleStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<UserRole> request) throws Exception {
        UserRole userRolePattern = request.getData();
        ServiceResponse response;
        if (userRolePattern == null) {
            response = new ServiceResponse<UserRole>("User role pattern information is not specified.", false);
        } else {
            Map<String, String> aliases = null;
            LinkedList<Criterion> restrictions = new LinkedList<Criterion>();
            Role role = userRolePattern.getRole();
            if (role != null) {
                if (role.getId() != null) {
                    restrictions.add(Restrictions.eq("role.id", role.getId()));
                } else if (StringUtils.isNotBlank(role.getLookup())) {
                    aliases = new HashMap<String, String>();
                    aliases.put("role", "role");
                    restrictions.add(Restrictions.eq("role.lookup", role.getLookup()));
                }
            }
            if (userRolePattern.getModelId() != null) {
                restrictions.add(Restrictions.eq("internalId", userRolePattern.getModelId()));
            } else if (StringUtils.isNotBlank(userRolePattern.getComplexPK())) {
                restrictions.add(Restrictions.eq("externalId", userRolePattern.getComplexPK()));
            }
            if (StringUtils.isNotBlank(userRolePattern.getTypeLookup())) {
                restrictions.add(Restrictions.eq("type", userRolePattern.getTypeLookup()));
            }
            BaseUser user = userRolePattern.getUser();
            if (user != null) {
                if (user.getId() != null) {
                    restrictions.add(Restrictions.eq("user.id", user.getId()));
                } else if (StringUtils.isNotBlank(user.getUsername())) {
                    if (aliases == null) {
                        aliases = new HashMap<String, String>();
                    }
                    aliases.put("user", "user");
                    restrictions.add(Restrictions.eq("user.username", user.getUsername()));
                }
            }
            List<UserRoleModel> userRolesFound = userRoleStore.search(restrictions, aliases, null, false);
            if (userRolesFound.isEmpty()) {
                response = new ServiceResponse("No user roles for specified pattern were found.", true);
            } else {
                userRoleStore.deleteAll(userRolesFound);
                response = new ServiceResponse("User Roles found and deleted by specified pattern.", true);
            }
        }
        return response;
    }

}