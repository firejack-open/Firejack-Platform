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
 * Class encapsulates the functionality of case description modification
 */
@TrackDetails
@Component("updateCaseDescriptionBroker")
public class UpdateCaseDescriptionBroker extends ServiceBroker<ServiceRequest<CaseOperationsParams>, ServiceResponse> {

    @Autowired
    @Qualifier("caseStore")
    private ICaseStore caseStore;

    /**
     * Invokes data access layer in order to update case description
     *
     * @param request service request containing ID and description of the case
     * @return service response containing the information about the modification success
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse perform(ServiceRequest<CaseOperationsParams> request) throws Exception {
        this.caseStore.update(request.getData().getCaseId(), request.getData().getCaseDescription());
        return new ServiceResponse("Case description has been updated.", true);
    }
    
}
