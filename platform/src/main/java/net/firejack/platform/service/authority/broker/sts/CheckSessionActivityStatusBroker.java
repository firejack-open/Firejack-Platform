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
import net.firejack.platform.core.broker.workflow.IWorkflowHandler;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;


@TrackDetails
@Component
public class CheckSessionActivityStatusBroker extends ServiceBroker
        <ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<SimpleIdentifier<Boolean>>> {

    @Override
    protected ServiceResponse<SimpleIdentifier<Boolean>> perform(
            ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        String sessionToken = request.getData().getIdentifier();
        Boolean sessionTokenActiveStatus;
        if (StringUtils.isBlank(sessionToken)) {
            sessionTokenActiveStatus = false;
        } else {
            sessionTokenActiveStatus = CacheManager.getInstance()
                    .getSessionTokenActiveStatus(sessionToken);
            sessionTokenActiveStatus = sessionTokenActiveStatus == null ? false : sessionTokenActiveStatus;
        }
        return new ServiceResponse<SimpleIdentifier<Boolean>>(
                new SimpleIdentifier<Boolean>(sessionTokenActiveStatus), null, true);
    }

    @Override
    public IWorkflowHandler getWorkflowHandler() {
        return null;
    }
}