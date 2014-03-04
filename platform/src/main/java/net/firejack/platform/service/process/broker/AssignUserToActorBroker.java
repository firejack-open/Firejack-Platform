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

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.IUserActorStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of assigning a user to an actor set
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("assignUserToActorBroker")
public class AssignUserToActorBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {

    @Autowired
    @Qualifier("userActorStore")
    private IUserActorStore userActorStore;

    /**
     * Assigns a user to an actor set
     *
     * @param request service request containing parameters for the assignment: IDs of the user and case (to whose actor set the user is added), actor lookup
     * @return service response containing information about the success of the assignment
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
        boolean isAssigned = userActorStore.assignUserToActor((Long)request.getData().get("caseId"), (Long)request.getData().get("userId"), (String)request.getData().get("actorLookup"));
        String msg = isAssigned ? "User assigned to the actor." : "Failed to assign user to the actor.";
        return new ServiceResponse(msg, isAssigned);
    }

}
