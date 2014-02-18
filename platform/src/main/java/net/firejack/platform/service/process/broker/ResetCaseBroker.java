package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseOperationsParams;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of resetting a case
 */
@TrackDetails
@Component("resetCaseBroker")
public class ResetCaseBroker extends ServiceBroker<ServiceRequest<CaseOperationsParams>, ServiceResponse> {

    @Autowired
    @Qualifier("caseStore")
    private ICaseStore caseStore;

    /**
     * Invokes data access layer in order to reset the case.
     * It is necessary to have the active task and for the user to be in the actor set.
     *
     * @param request service request containing parameters for resseting the case
     * @return service response containing info about the success of the operation
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse perform(ServiceRequest<CaseOperationsParams> request) throws Exception {
        ServiceResponse response;
        if (ContextManager.isUserAuthenticated()) {
            try {
                caseStore.reset(request.getData().getCaseId(), request.getData().getNoteText(), null);
                response = new ServiceResponse("Case reset successful.", true);
            } catch (UserNotInActorSetException e) {
                response = new ServiceResponse("User is not in the actor set for the activity.", false);
            } catch (TaskNotActiveException e) {
                response = new ServiceResponse("Task is not active.", false);
            }
        } else {
            response = new ServiceResponse("Guest user doesn't have access to cases related data.", false);
        }
        return response;
    }
    
}
