package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Actor;
import net.firejack.platform.core.broker.ReadBroker;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of retrieving the actor
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readActorBroker")
public class ReadActorBroker extends ReadBroker<ActorModel, Actor> {

    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;

    @Override
    protected IStore<ActorModel, Long> getStore() {
        return actorStore;
    }
    
}
