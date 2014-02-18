package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseAction;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.CaseActionModel;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ICaseActionStore;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of retrieving case actions by case
 */
@TrackDetails
@Component("readCaseActionsByCaseBroker")
public class ReadCaseActionsByCaseBroker extends ListBroker<CaseActionModel, CaseAction, SimpleIdentifier<Long>> {

    @Autowired
    @Qualifier("caseStore")
    private ICaseStore caseStore;

    @Autowired
    @Qualifier("caseActionStore")
    protected ICaseActionStore caseActionStore;

    /**
     * Invokes data access layer in order to find the case by its' ID
     * and then case actions by the found case
     * @param simpleIdentifierServiceRequest service request containing ID of the case
     * @return list of found case actions
     * @throws BusinessFunctionException
     */
    @Override
    protected List<CaseActionModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> simpleIdentifierServiceRequest) throws BusinessFunctionException {
        List<CaseActionModel> caseActionModels = null;
        CaseModel processCase = caseStore.findById(simpleIdentifierServiceRequest.getData().getIdentifier());
        if (processCase != null) {
            caseActionModels = caseActionStore.findAllByCase(processCase.getId());
        }
        return caseActionModels;
    }
    
}
