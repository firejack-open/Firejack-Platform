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
