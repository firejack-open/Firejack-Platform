package net.firejack.platform.service.process.broker;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of checking if a user belongs to a certain actor set
 */
@TrackDetails
@Component("checkIfUserIsActorBroker")
public class CheckIfUserIsActorBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<SimpleIdentifier<Boolean>>> {

    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;

    /**
     * Checks if currently logged in user is in the actor set
     *
     * @param request service request containing ID of the actor
     * @return service response containing information about the user being in the actor set
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<SimpleIdentifier<Boolean>> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        ActorModel actor = actorStore.findByLookup(request.getData().getIdentifier());
        OpenFlamePrincipal principal = OPFContext.getContext().getPrincipal();
        boolean currentUserIsActor = false;
        if (actor != null) {
            currentUserIsActor = actorStore.isUserInActorSet(principal.getUserInfoProvider().getId(), actor.getId());
        }
        return new ServiceResponse<SimpleIdentifier<Boolean>>(new SimpleIdentifier<Boolean>(currentUserIsActor), "Current user checked.", true);
    }
    
}
