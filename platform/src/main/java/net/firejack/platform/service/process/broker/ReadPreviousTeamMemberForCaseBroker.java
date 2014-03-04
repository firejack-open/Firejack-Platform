/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.NoPreviousActivityException;
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
 * Class encapsulates the functionality of retrieving previous team member for the case
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readPreviousTeamMemberForCaseBroker")
public class ReadPreviousTeamMemberForCaseBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<User>> {

    @Autowired
    @Qualifier("userActorStore")
    protected IUserActorStore userActorStore;

    /**
     * Invokes data access layer in order to find previous team member for the case.
     * It is necessary to have active task and previous activity.
     *
     * @param request service request containing ID of the case
     * @return service response containing info about the success of the operation and user who is previous team member if found
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<User> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
        User user;
        try {
            UserModel userModel = userActorStore.findPreviousTeamMemberForCase(request.getData().getIdentifier());
            user = factory.convertTo(User.class, userModel);
        } catch (TaskNotActiveException e) {
            return new ServiceResponse<User>("Error: No active task.", false);
        } catch (NoPreviousActivityException e) {
            return new ServiceResponse<User>("Error: No previous activity.", false);
        }
        return user == null ?
                new ServiceResponse<User>("Previous team member was not found", true) :
                new ServiceResponse<User>(user, "Previous team member has been found by case id: " + request.getData().getIdentifier(), true);
    }
}
