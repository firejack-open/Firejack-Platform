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
