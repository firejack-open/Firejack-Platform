package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseExplanation;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.CaseExplanationModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ICaseExplanationStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of searching for the explanations
 */
@TrackDetails
@Component("searchProcessExplanationsBroker")
public class SearchProcessExplanationsBroker extends ListBroker<CaseExplanationModel, CaseExplanation, NamedValues> {

    @Autowired
    @Qualifier("caseExplanationStore")
    private ICaseExplanationStore store;

    /**
     * Invokes data access layer in order to find explanations by search term and process ID
     * @param namedValuesServiceRequest service request containing the search term and ID of the process
     * @return list of found explanations
     * @throws BusinessFunctionException
     */
    @Override
    protected List<CaseExplanationModel> getModelList(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        Long processId = (Long)namedValuesServiceRequest.getData().get("processId");
        String term = (String)namedValuesServiceRequest.getData().get("term");
        return store.findProcessExplanationsBySearchTerm(processId, term, "shortDescription", "ASC");
    }
    
}
