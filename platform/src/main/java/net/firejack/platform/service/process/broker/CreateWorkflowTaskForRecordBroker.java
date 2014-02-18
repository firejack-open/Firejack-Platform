package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createWorkflowTaskForRecordBroker")
public class CreateWorkflowTaskForRecordBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Task>> {

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IProcessStore processStore;
    @Autowired
    private ITaskStore taskStore;

    @Override
    protected ServiceResponse<Task> perform(ServiceRequest<NamedValues> request) throws Exception {
        Long recordId = (Long) request.getData().get("recordId");
        String entityLookup = (String) request.getData().get("entityLookup");
        Long processId = (Long) request.getData().get("processId");

        ServiceResponse<Task> response;

        EntityModel entityModel = entityStore.findByLookup(entityLookup);
        if (entityModel != null) {
            ProcessModel processModel = processStore.findById(processId);
            if (processModel != null) {
                TaskModel taskModel = taskStore.createWorkflowTask(processModel, entityModel, recordId);
                Task task = factory.convertTo(Task.class, taskModel);
                response = new ServiceResponse<Task>(task, "Workflow has been created for Entity: '" + entityModel.getLookup() + "' with ID: " + recordId, true);
            } else {
                response = new ServiceResponse<Task>("Process has not been found for Entity: " + entityModel.getLookup() + " by process id: " + processId, false);
            }
        } else {
            response = new ServiceResponse<Task>("Couldn't find Entity by lookup: " + entityLookup, false);
        }
        return response;
    }

}
