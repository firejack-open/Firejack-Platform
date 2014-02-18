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

package net.firejack.platform.service.directory.broker.systemuser;

import net.firejack.platform.api.directory.domain.SystemUser;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("generateConsumerSecretBroker")
public class GenerateConsumerSecretBroker
        extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<SystemUser>> {

    @Override
    protected ServiceResponse<SystemUser> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws BusinessFunctionException {
        SystemUser systemUser = new SystemUser();
        String consumerSecret = SecurityHelper.generateRandomSequence(32);
        systemUser.setConsumerSecret(consumerSecret);
        return new ServiceResponse<SystemUser>(systemUser, "Regenerated Consumer Secret Key", true);
    }

}
