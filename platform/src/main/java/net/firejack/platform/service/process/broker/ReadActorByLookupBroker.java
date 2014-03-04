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

import net.firejack.platform.api.process.domain.Actor;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of retrieving the actor by its' lookup
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readActorByLookupBroker")
public class ReadActorByLookupBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<Actor>>{

    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;

    /**
     * Invokes data access layer in order to find the actor by its' lookup.
     * The found entity is then converted to an actor data transfer object and incorporated in the service response.
     *
     * @param request service request containing lookup of the actor
     * @return service response containing the information about the retrieval success and the actor if found
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<Actor> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        ActorModel actorModel = actorStore.findByLookup(request.getData().getIdentifier());
        Actor actor = factory.convertTo(Actor.class, actorModel);
        return new ServiceResponse<Actor>(actor, "Actor found by lookup: " + request.getData().getIdentifier(), true);
    }
    
}
