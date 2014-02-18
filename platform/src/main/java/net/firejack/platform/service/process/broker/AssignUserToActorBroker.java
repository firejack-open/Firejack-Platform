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
