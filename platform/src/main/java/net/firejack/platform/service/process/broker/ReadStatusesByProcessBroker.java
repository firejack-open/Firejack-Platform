package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Status;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.StatusModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.IStatusStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of statuses retrieval by process
 */
@TrackDetails
@Component("readStatusesByProcessBroker")
public class ReadStatusesByProcessBroker extends ListBroker<StatusModel, Status, SimpleIdentifier<Long>> {

    @Autowired
    private IStatusStore store;

    /**
     * Invokes data access layer in order to retrieve statuses by process
     * @param simpleIdentifierServiceRequest service request connatining ID of the process
     * @return list of found statuses
     * @throws BusinessFunctionException
     */
    @Override
    protected List<StatusModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> simpleIdentifierServiceRequest) throws BusinessFunctionException {
        return store.findByProcessId(simpleIdentifierServiceRequest.getData().getIdentifier(), getFilter());
    }
    
}
