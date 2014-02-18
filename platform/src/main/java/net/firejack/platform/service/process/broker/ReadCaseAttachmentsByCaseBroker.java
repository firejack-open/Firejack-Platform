package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseAttachment;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.CaseAttachmentModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ICaseAttachmentStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of retrieving case attachments by case
 */
@TrackDetails
@Component("readCaseAttachmentsByCaseBroker")
public class ReadCaseAttachmentsByCaseBroker extends ListBroker<CaseAttachmentModel, CaseAttachment, SimpleIdentifier<Long>> {

    @Autowired
    @Qualifier("caseAttachmentStore")
    private ICaseAttachmentStore store;

    /**
     * Invokes data access layer in order to search for the attachments by the case
     * @param simpleIdentifierServiceRequest service request containing ID of the case
     * @return list of found attachments
     * @throws BusinessFunctionException
     */
    @Override
    protected List<CaseAttachmentModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> simpleIdentifierServiceRequest) throws BusinessFunctionException {
        return store.findByCaseIdAndSearchTerm(simpleIdentifierServiceRequest.getData().getIdentifier(), null);
    }
    
}
