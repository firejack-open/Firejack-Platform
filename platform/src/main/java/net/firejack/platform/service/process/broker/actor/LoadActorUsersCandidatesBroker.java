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

package net.firejack.platform.service.process.broker.actor;

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@TrackDetails
public class LoadActorUsersCandidatesBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<User>> {

    @Autowired
    @Qualifier("userStore")
    private IUserStore userStore;

    @Override
    protected ServiceResponse<User> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
        Long actorId = request.getData().getIdentifier();
        ServiceResponse<User> response;
        if (actorId == null) {
            response = new ServiceResponse<User>("Actor Id should not be null.", false);
        } else {
            List<UserModel> users = userStore.findUsersBelongingToActorNotInCase(actorId);
            if (users == null || users.isEmpty()) {
                response = new ServiceResponse<User>("No users were found assigned to the specified actor.", true);
            } else {
                List<User> actorCandidateUsers = new ArrayList<User>();
                for (UserModel userModel : users) {//provide only necessary information
                    User user = new User();
                    user.setId(userModel.getId());
                    user.setUsername(userModel.getUsername());
                    user.setFirstName(userModel.getFirstName());
                    user.setLastName(userModel.getLastName());
                    user.setMiddleName(userModel.getMiddleName());
                    actorCandidateUsers.add(user);
                }
                response = new ServiceResponse<User>(actorCandidateUsers, "Actor candidates were found", true);
            }
        }
        return response;
    }

}