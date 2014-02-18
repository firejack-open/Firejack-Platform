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
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.IUserActorStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of retrieving next team member for the case
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readNextTeamMemberForCaseBroker")
public class ReadNextTeamMemberForCaseBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<User>> {

    @Autowired
    @Qualifier("userActorStore")
    protected IUserActorStore userActorStore;

    /**
     * Invokes data access layer in order to find next team member for the case.
     * It is necessary to have the active task.
     *
     * @param request service request containing ID of the case
     * @return service response containing info about the success of the operation and user who is next team member if found
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<User> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
        User user;
        try {
            UserModel userModel = userActorStore.findNextTeamMemberForCase(request.getData().getIdentifier());
            user = factory.convertTo(User.class, userModel);
        } catch (TaskNotActiveException e) {
            return new ServiceResponse<User>("Error: No active task.", false);
        }
        return user == null ?
                new ServiceResponse<User>("Next team member was not found by case Id.", true) :
                new ServiceResponse<User>(user, "Next team member has been found by case id: " +
                        request.getData().getIdentifier(), true);
    }
    
}
