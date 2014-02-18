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

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.broker.securedrecord.ISecuredRecordHandler;
import net.firejack.platform.core.broker.security.SecurityHandler;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRoleStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.SystemPrincipal;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@TrackDetails
@Component
public class SynchronizeSMAdminsBroker extends ServiceBroker
        <ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<SimpleIdentifier<String>>> {

    private static final String ADMIN_ROLE_LOOKUP = "net.firejack.platform.admin";
    private static final String USER_ROLE_LOOKUP = "net.firejack.platform.user";

    @Autowired
    private IUserStore userStore;

    @Autowired
    private IRoleStore roleStore;

    @Override
    protected ServiceResponse<SimpleIdentifier<String>> perform(ServiceRequest<SimpleIdentifier<String>> request)
            throws Exception {
        ServiceResponse<SimpleIdentifier<String>> response;
        OPFContext context;
        try {
            context = OPFContext.getContext();
        } catch (ContextLookupException e) {
            context = null;
        }
        //method could be called within application it self or only by system user.
        if (context == null || !(context.getPrincipal() instanceof SystemPrincipal)) {
            response = new ServiceResponse<SimpleIdentifier<String>>(
                    "User is not authorized to synchronize SiteMinder admin users.", true);
        } else {
            String newSmAdmins = request.getData().getIdentifier();
            Set<String> newSmAdminSet;
            if (StringUtils.isBlank(newSmAdmins)) {
                newSmAdminSet = new HashSet<String>(0);
            } else {
                newSmAdminSet = new HashSet<String>();
                String[] usernames = newSmAdmins.split(",");
                for (String username : usernames) {
                    if (StringUtils.isNotBlank(username)) {
                        newSmAdminSet.add(username.trim());
                    }
                }
            }
            Set<String> oldSmAdminSet = CacheManager.getInstance().getSiteMinderAdmins();
            oldSmAdminSet = oldSmAdminSet == null ? new HashSet<String>() : oldSmAdminSet;

            //maybe it's not necessary - to load all admin users and remove admin role
            if (oldSmAdminSet.isEmpty()) {
                RoleModel adminRole = roleStore.findByLookup(ADMIN_ROLE_LOOKUP);
                if (adminRole == null) {
                    logger.error("OpenFlame admin role is not exist.");
                } else {
                    List<Long> roleIdList = new ArrayList<Long>(1);
                    roleIdList.add(adminRole.getId());
                    List<UserModel> adminUsers = userStore.findByRoles(roleIdList);
                    if (adminUsers != null) {
                        for (UserModel adminUser : adminUsers) {
                            oldSmAdminSet.add(adminUser.getUsername());
                        }
                    }
                }
            }

            Set<String> usersToDowngrade = new HashSet<String>();
            Set<String> usersToUpgrade = new HashSet<String>();

            for (String oldAdmin : oldSmAdminSet) {
                if (!newSmAdminSet.contains(oldAdmin)) {
                    usersToDowngrade.add(oldAdmin);
                }
            }

            for (String newAdmin : newSmAdminSet) {
                if (!oldSmAdminSet.contains(newAdmin)) {
                    usersToUpgrade.add(newAdmin);
                }
            }

            if (!usersToDowngrade.isEmpty()) {
                updateUserRoles(usersToDowngrade, USER_ROLE_LOOKUP);
            }

            if (!usersToUpgrade.isEmpty()) {
                updateUserRoles(usersToUpgrade, ADMIN_ROLE_LOOKUP);
            }

            CacheManager.getInstance().setSiteMinderAdmins(newSmAdminSet);

            response = new ServiceResponse<SimpleIdentifier<String>>("Success.", true);
        }

        return response;
    }

    protected SecurityHandler getSecurityHandler() {
        return null;
    }

    @Override
    protected ISecuredRecordHandler getSecuredRecordHandler() {
        return null;
    }

    private void updateUserRoles(Collection<String> usernameCollection, String newRoleLookup) {
        LinkedList<Criterion> criterionList = new LinkedList<Criterion>();
        criterionList.add(Restrictions.in("username", usernameCollection));
        List<UserModel> foundUsers = userStore.search(criterionList, null);
        RoleModel role = roleStore.findByLookup(newRoleLookup);
        for (UserModel user : foundUsers) {
            List<UserRoleModel> newUserRoles = new ArrayList<UserRoleModel>(role == null ? 0 : 1);
            if (role != null) {
                newUserRoles.add(new UserRoleModel(user, role));
            }
            user.setUserRoles(newUserRoles);
            userStore.save(user);
        }
    }

}