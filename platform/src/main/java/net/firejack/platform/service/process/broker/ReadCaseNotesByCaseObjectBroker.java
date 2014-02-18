package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseNote;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.CaseNoteModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ICaseNoteStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of retrieving case notes by case object
 */
@TrackDetails
@Component("readCaseNotesByCaseObjectBroker")
public class ReadCaseNotesByCaseObjectBroker extends ListBroker<CaseNoteModel, CaseNote, NamedValues> {

    @Autowired
    private ICaseNoteStore store;

    /**
     * Invokes data access layer in order to search for the case notes by the case object
     * @param namedValuesServiceRequest service request containing ID and lookup of the case object
     * @return list of found case notes
     * @throws BusinessFunctionException
     */
    @Override
    protected List<CaseNoteModel> getModelList(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        Long entityId = (Long)namedValuesServiceRequest.getData().get("entityId");
        String entityType = (String)namedValuesServiceRequest.getData().get("entityType");
        return store.findByCaseObjectAndSearchTerm(entityId, entityType, null);
    }
    
}
