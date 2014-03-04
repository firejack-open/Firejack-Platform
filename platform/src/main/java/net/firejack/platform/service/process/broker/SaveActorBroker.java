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
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.store.registry.IActorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Class encapsulates the functionality of persisting an actor
 */
public abstract class SaveActorBroker extends OPFSaveBroker<ActorModel, Actor, Actor> {

    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;

    /**
     * Converts an actor data transfer object to an actor entity
     * @param actor data transfer object for an actor
     * @return actor entity
     */
    @Override
    protected ActorModel convertToEntity(Actor actor) {
        return factory.convertFrom(ActorModel.class, actor);
    }

    /**
     * Converts an actor entity to an actor data transfer object
     * @param entity actor entity
     * @return actor data transfer object
     */
    @Override
    protected Actor convertToModel(ActorModel entity) {
        return factory.convertTo(Actor.class, entity);
    }

    /**
     * Invokes data access layer on order to persist an actor
     *
     * @param actorModel actor entity to be saved
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected void save(ActorModel actorModel) throws Exception {
        actorStore.save(actorModel);
    }

}
