package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Status;
import net.firejack.platform.core.broker.ReadBroker;
import net.firejack.platform.core.model.registry.process.StatusModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.process.IStatusStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of status retrieval
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readStatusBroker")
public class ReadStatusBroker extends ReadBroker<StatusModel, Status> {

    @Autowired
    @Qualifier("statusStore")
    private IStatusStore statusStore;

    @Override
    protected IStore<StatusModel, Long> getStore() {
        return statusStore;
    }

}
