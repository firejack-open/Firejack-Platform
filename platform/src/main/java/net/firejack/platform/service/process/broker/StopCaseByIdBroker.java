package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseOperationsParams;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of stopping a case by its' ID
 */
@TrackDetails
@Component("stopCaseByIdBroker")
public class StopCaseByIdBroker extends ServiceBroker<ServiceRequest<CaseOperationsParams>, ServiceResponse> {

    @Autowired
    @Qualifier("caseStore")
    private ICaseStore caseStore;

    /**
     * Invokes data access layer in order to stop the case
     *
     * @param request service request containing note and IDs of the case and explanation
     * @return service response containing the information about the success of the operation
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse perform(ServiceRequest<CaseOperationsParams> request) throws Exception {
        try {
            caseStore.stop(request.getData().getCaseId(), request.getData().getExplanationId(), request.getData().getNoteText());
            return new ServiceResponse("Case stopped successfully.", true);
        } catch (Exception e) {
            logger.error("Error stopping the case", e);
            return new ServiceResponse("Error stopping the case.", false);
        }
    }
    
}
