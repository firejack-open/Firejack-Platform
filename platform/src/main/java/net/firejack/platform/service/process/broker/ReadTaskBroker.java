package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.core.broker.ReadBroker;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of task retrieval
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readTaskBroker")
public class ReadTaskBroker extends ReadBroker<TaskModel, Task> {

    @Autowired
    @Qualifier("taskStore")
    private ITaskStore taskStore;

    @Override
    protected IStore<TaskModel, Long> getStore() {
        return taskStore;
    }
    
}
