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

package net.firejack.platform.service.authority.broker.sts;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.broker.securedrecord.ISecuredRecordHandler;
import net.firejack.platform.core.broker.security.SecurityHandler;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.SystemPrincipal;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@Component
@TrackDetails
public class UpdateSMUserRolesBroker extends ServiceBroker<ServiceRequest<User>, ServiceResponse<User>> {

    @Autowired
    private IUserStore userStore;

    @Autowired
    private IRoleStore roleStore;

    @Override
    protected ServiceResponse<User> perform(ServiceRequest<User> request) throws Exception {
        ServiceResponse<User> response;
        OPFContext context;
        try {
            context = OPFContext.getContext();
        } catch (ContextLookupException e) {
            context = null;
        }
        //method could be called within application it self or only by system user.
        if (context != null && !(context.getPrincipal() instanceof SystemPrincipal)) {
            response = new ServiceResponse<User>(
                    "User is not authorized to synchronize SiteMinder admin users.", true);
        } else {
            User user = request.getData();
            if (user == null) {
                response = new ServiceResponse<User>("User to update is null.", false);
            } else if (StringUtils.isBlank(user.getUsername())) {
                response = new ServiceResponse<User>("User to update has empty username.", false);
            } else {
                String standardId = user.getUsername();

                UserModel smUser = userStore.findUserByUsername(standardId);
                if (smUser == null) {
                    //we will not create smUser for now.
                    response = new ServiceResponse<User>("User is not registered in opf database yet.", true);
                } else {
                    //List<UserRole> userRoles = user.getUserRoles();
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

                    List<UserRoleModel> newUserRoles = new ArrayList<UserRoleModel>(roles == null ? 0 : roles.size());
                    if (roles != null) {
                        for (RoleModel role : roles) {
                            newUserRoles.add(new UserRoleModel(smUser, role));
                        }
                    }
                    smUser.setUserRoles(newUserRoles);
                    userStore.save(smUser);

                    response = new ServiceResponse<User>("User roles updated successfully.", true);
                }
            }
        }
        return response;
    }

    @Override
    protected SecurityHandler getSecurityHandler() {
        return null;
    }

    @Override
    protected ISecuredRecordHandler getSecuredRecordHandler() {
        return null;
    }

    @Override
    protected void validateArguments(ServiceRequest<User> request) throws RuleValidationException {
        //
    }
}