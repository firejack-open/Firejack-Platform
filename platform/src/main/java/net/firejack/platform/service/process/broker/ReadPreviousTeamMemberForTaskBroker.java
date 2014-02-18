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
import net.firejack.platform.core.exception.NoPreviousActivityException;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.IUserActorStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of retrieving previous team member for the task
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readPreviousTeamMemberForTaskBroker")
public class ReadPreviousTeamMemberForTaskBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<User>> {

    @Autowired
    @Qualifier("userActorStore")
    protected IUserActorStore userActorStore;

    /**
     * Invokes data access layer in order to find previous team member for the task.
     * It is necessary to have previous activity.
     *
     * @param request service request containing ID of the task
     * @return service response containing info about the success of the operation and user who is previous team member if found
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<User> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
        UserModel userModel;
        try {
            userModel = userActorStore.findPreviousTeamMemberForTask(request.getData().getIdentifier());
        } catch (NoPreviousActivityException e) {
            return new ServiceResponse<User>("Error: No previous activity.", false);
        }
        User user = factory.convertTo(User.class, userModel);
        return user == null ?
                new ServiceResponse<User>("The previous team member was not found.", true) :
                new ServiceResponse<User>(user, "The previous team member for task with ID: " +
                        request.getData().getIdentifier() + " found successfully.", true);
    }
    
}
