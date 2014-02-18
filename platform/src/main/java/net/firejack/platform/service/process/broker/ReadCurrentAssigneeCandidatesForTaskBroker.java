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

package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of retrieving current assignee candidates for the task
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readCurrentAssigneeCandidatesForTaskBroker")
public class ReadCurrentAssigneeCandidatesForTaskBroker extends ListBroker<UserModel, User, SimpleIdentifier<Long>> {

    @Autowired
    @Qualifier("actorStore")
    private IActorStore store;

    /**
     * Invokes data access layer in order to find current assignee list for the task
     * @param simpleIdentifierServiceRequest service request containing ID of the task
     * @return list of users who are current assignee candidates for the task
     * @throws BusinessFunctionException
     */
    @Override
    protected List<UserModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> simpleIdentifierServiceRequest) throws BusinessFunctionException {
        return store.findTaskAssigneeList(simpleIdentifierServiceRequest.getData().getIdentifier(), null);
    }
    
}
