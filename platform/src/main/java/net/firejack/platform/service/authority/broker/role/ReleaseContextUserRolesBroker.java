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
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@TrackDetails
public class ReleaseContextUserRolesBroker extends ServiceBroker<ServiceRequest<UserRole>, ServiceResponse> {

    @Autowired
    private IRoleStore roleStore;
    @Autowired
    private IUserRoleStore userRoleStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<UserRole> request) throws Exception {
        List<UserRole> userRoles = request.getDataList();
        ServiceResponse response;
        if (userRoles == null || userRoles.isEmpty()) {
            response = new ServiceResponse<UserRole>("No user role information specified.", false);
        } else {
            OPFContext context = OPFContext.getContext();
            if (context == null || context.getPrincipal().isGuestPrincipal()) {
                response = new ServiceResponse<UserRole>("Guest user is not authorized to release context roles.", false);
            } else {
                Set<String> lookupList = new HashSet<String>();
                LinkedList<UserRole> userRolesValidated = new LinkedList<UserRole>();
                for (UserRole userRole : userRoles) {
                    Role role = userRole.getRole();
                    BaseUser user = userRole.getUser();
                    if (role != null && StringUtils.isNotBlank(role.getLookup()) && user != null &&
                            user.getId() != null && StringUtils.isNotBlank(userRole.getTypeLookup()) &&
                            (userRole.getModelId() != null || StringUtils.isNotBlank(userRole.getComplexPK()))) {
                        userRolesValidated.add(userRole);
                        lookupList.add(role.getLookup());
                    }
                }
                if (!lookupList.isEmpty()) {
                    LinkedList<Criterion> restrictions = new LinkedList<Criterion>();
                    restrictions.add(Restrictions.in("lookup", lookupList));
                    List<Object[]> roles = roleStore.searchWithProjection(restrictions,
                            Projections.projectionList()
                                    .add(Projections.property("lookup"))
                                    .add(Projections.id()), null);
                    restrictions.clear();
                    Map<String, Long> rolesMap = new HashMap<String, Long>();
                    for (Object[] roleModelData : roles) {
                        rolesMap.put((String) roleModelData[0], (Long) roleModelData[1]);
                    }
                    LinkedList<UserRoleModel> userRoleList = new LinkedList<UserRoleModel>();
                    for (UserRole userRole : userRolesValidated) {
                        Role role = userRole.getRole();
                        if (role != null && StringUtils.isNotBlank(role.getLookup())) {
                            Long roleId = rolesMap.get(role.getLookup());
                            if (roleId != null) {
                                restrictions.add(Restrictions.eq("user.id", userRole.getUser().getId()));
                                restrictions.add(Restrictions.eq("role.id", roleId));
                                restrictions.add(Restrictions.eq("type", userRole.getTypeLookup()));
                                if (userRole.getModelId() == null) {
                                    restrictions.add(Restrictions.eq("externalId", userRole.getComplexPK()));
                                } else {
                                    restrictions.add(Restrictions.eq("internalId", userRole.getModelId()));
                                }
                                List<UserRoleModel> foundUserRoles = userRoleStore.search(restrictions, null, null, false);
                                if (!foundUserRoles.isEmpty()) {
                                    userRoleList.add(foundUserRoles.get(0));
                                }
                                restrictions.clear();
                            }
                        }
                    }
                    userRoleStore.deleteAll(userRoleList);
                }
                response = new ServiceResponse<UserRole>("Success", true);
            }
        }
        return response;
    }

}