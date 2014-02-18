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

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.RoleStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@TrackDetails
public class AllowInstantSecurityBroker extends ServiceBroker<ServiceRequest<NamedValues<Object>>, ServiceResponse> {

    public static final String PARAM_ENTITY_LOOKUP = "entityLookup";
    public static final String PARAM_SECURITY_ENABLED = "securityEnabled";
    private static final String MSG_SECURITY_VIOLATION =
            "Current User is not permitted to change the Instant Security settings for the package.";

    @Autowired
    private RoleStore roleStore;

    @Autowired
    private IEntityStore entityStore;

    @Override
    protected ServiceResponse perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        String entityLookup = (String) request.getData().get(PARAM_ENTITY_LOOKUP);
        Boolean securityEnabled = (Boolean) request.getData().get(PARAM_SECURITY_ENABLED);
        ServiceResponse response;
        if (StringUtils.isBlank(entityLookup)) {
            response = new ServiceResponse("Entity lookup is not specified.", false);
        } else if (securityEnabled == null) {
            response = new ServiceResponse("Security Enabled flag is not specified.", false);
        } else {
            OPFContext context;
            try {
                context = OPFContext.getContext();
            } catch (ContextLookupException e) {
                context = null;
            }
            if (context == null || context.getPrincipal().isGuestPrincipal()) {
                response = new ServiceResponse(MSG_SECURITY_VIOLATION, false);
            } else {
                CacheManager cacheManager = CacheManager.getInstance();
                Tuple<User, List<Long>> userRoles = cacheManager.getUserInfo(context.getSessionToken());
                if (userRoles == null || userRoles.getValue() == null || userRoles.getValue().isEmpty()) {
                    response = new ServiceResponse(MSG_SECURITY_VIOLATION, false);
                } else {
                    EntityModel entityModel = entityStore.findByLookup(entityLookup);
                    if (entityModel == null) {
                        response = new ServiceResponse("There is no entity for specified lookup value.", false);
                    } else {
                        String packageAdminRole = StringUtils.getPackageLookup(entityLookup) + ".admin";
                        RoleModel adminRole = roleStore.findByLookup(packageAdminRole);
                        if (adminRole == null || !userRoles.getValue().contains(adminRole.getId())) {
                            response = new ServiceResponse(MSG_SECURITY_VIOLATION, false);
                        } else {
                            entityModel.setSecurityEnabled(securityEnabled);
                            entityStore.save(entityModel);
                            response = new ServiceResponse("Security Enabled flag is set.", true);
                        }
                    }
                }
            }
        }
        return response;
    }

}