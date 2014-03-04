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

import java.util.*;

/**
 * Class encapsulates the functionality of retrieving next assignee candidates for the task
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readNextAssigneeCandidatesForTaskBroker")
public class ReadNextAssigneeCandidatesForTaskBroker extends ListBroker<UserModel, User, SimpleIdentifier<Long>> {

    @Autowired
    @Qualifier("actorStore")
    private IActorStore store;

    /**
     * Invokes data access layer in order to retrieve the list of next assignee candidates for the task
     * @param request service request containing ID of the task
     * @return list of users who are next assignee candidates for the task
     * @throws BusinessFunctionException
     */
    @Override
    protected List<UserModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> request) throws BusinessFunctionException {
        Long taskId = request.getData().getIdentifier();
        List<UserModel> taskAssigneeCandidates = store.findTaskAssigneeList(taskId, Boolean.TRUE);
        List<UserModel> result;
        if (taskAssigneeCandidates == null) {
            result = Collections.emptyList();
        } else {
            Set<UserModel> distinctCollection = new HashSet<UserModel>(taskAssigneeCandidates);
            result = new ArrayList<UserModel>(distinctCollection);
        }
        return result;
    }
    
}
