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

import net.firejack.platform.api.directory.domain.UserRole;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.user.IUserRoleStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


@Component
@TrackDetails
public class CheckUserIsOwnerBroker extends ServiceBroker
        <ServiceRequest<UserRole>, ServiceResponse<SimpleIdentifier<Boolean>>> {

    @Autowired
    private IUserRoleStore userRoleStore;

    @Override
    protected ServiceResponse<SimpleIdentifier<Boolean>> perform(ServiceRequest<UserRole> request) throws Exception {
        ServiceResponse<SimpleIdentifier<Boolean>> response;
        UserRole userRole = request.getData();
        if (userRole == null || StringUtils.isBlank(userRole.getTypeLookup()) ||
                (userRole.getModelId() == null && StringUtils.isBlank(userRole.getComplexPK()))) {
            response = new ServiceResponse<SimpleIdentifier<Boolean>>("Not enough input parameters are specified.", false);
        } else {
            OPFContext context;
            try {
                context = OPFContext.getContext();
            } catch (ContextLookupException e) {
                context = null;
            }
            if (context == null || context.getPrincipal().isGuestPrincipal()) {
                response = new ServiceResponse<SimpleIdentifier<Boolean>>("Guest user could not be an object owner.", false);
            } else {
                Map<String, String> aliases = new HashMap<String, String>();
                aliases.put("role", "role");
                String roleLookup = userRole.getTypeLookup() + ".owner";
                Long currentUserId = context.getPrincipal().getUserInfoProvider().getId();

                LinkedList<Criterion> restrictions = new LinkedList<Criterion>();
                restrictions.add(Restrictions.eq("type", userRole.getTypeLookup()));
                restrictions.add(Restrictions.eq("user.id", currentUserId));
                restrictions.add(Restrictions.eq("role.lookup", roleLookup));
                if (userRole.getModelId() == null) {
                    restrictions.add(Restrictions.eq("externalId", userRole.getComplexPK()));
                } else {
                    restrictions.add(Restrictions.eq("internalId", userRole.getModelId()));
                }
                Integer recordsCount = userRoleStore.searchCount(restrictions, aliases, false);
                SimpleIdentifier<Boolean> isOwner = new SimpleIdentifier<Boolean>(recordsCount != null && recordsCount > 0);

                response = new ServiceResponse<SimpleIdentifier<Boolean>>(isOwner, "Success", true);
            }
        }
        return response;
    }

}