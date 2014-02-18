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
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.session.UserSessionManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;


@TrackDetails
@Component
public class CheckSessionExpirationBroker extends ServiceBroker
        <ServiceRequest, ServiceResponse<SimpleIdentifier<Boolean>>> {

    @Override
    protected ServiceResponse<SimpleIdentifier<Boolean>> perform(ServiceRequest request)
		    throws Exception {
        try {
            OPFContext context = OPFContext.getContext();
            Boolean expired = StringUtils.isBlank(context.getSessionToken()) ||
                    isSessionExpired(context.getSessionToken());
            return new ServiceResponse<SimpleIdentifier<Boolean>>(new SimpleIdentifier<Boolean>(expired), null, true);
        } catch (ContextLookupException e) {
            logger.error(e.getMessage());
            return new ServiceResponse<SimpleIdentifier<Boolean>>(
                    new SimpleIdentifier<Boolean>(true), e.getMessage(), true);
        }
    }

    private boolean isSessionExpired(String sessionToken) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        boolean result;
        if (sessionManager.isUserSessionExpired(sessionToken)) {
            result = true;//signal that session expired
        } else {
            sessionManager.prolongUserSessionExpiration(
                    sessionToken,  Long.parseLong(OpenFlameConfig.SESSION_EXPIRATION_PERIOD.getValue()));
            result = false;
        }
        return result;
    }

}