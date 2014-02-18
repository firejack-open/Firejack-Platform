package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Activity;
import net.firejack.platform.api.process.domain.ActivityAction;
import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.process.*;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.IActivityActionStore;
import net.firejack.platform.core.store.process.IActivityFieldStore;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("readWorkflowTasksForRecordBroker")
public class ReadWorkflowTasksForRecordBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Task>> {

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IProcessStore processStore;
    @Autowired
    private ITaskStore taskStore;
    @Autowired
    private IActivityActionStore activityActionStore;
    @Autowired
    private IActivityFieldStore activityFieldStore;

    @Override
    protected ServiceResponse<Task> perform(ServiceRequest<NamedValues> request) throws Exception {
        Long recordId = (Long) request.getData().get("recordId");
        String entityLookup = (String) request.getData().get("entityLookup");

        ServiceResponse<Task> response;

        EntityModel entityModel = entityStore.findByLookup(entityLookup);
        if (entityModel != null) {
            List<ProcessModel> processModels = processStore.findAllByMainId(entityModel.getId());
            if (processModels.size() > 0) {
                List<Task> tasks = new ArrayList<Task>();
                for (ProcessModel processModel : processModels) {
                    TaskModel taskModel = taskStore.readWorkflowTask(processModel, entityModel, recordId);
                    if (taskModel != null) {
                        Task task = factory.convertTo(Task.class, taskModel);

                        Activity activity = task.getActivity();
                        List<ActivityActionModel> activityActionModels = activityActionStore.findActionsFromActivity(activity.getId());
                        for (ActivityActionModel activityActionModel : activityActionModels) {
                            ActivityModel activityTo = activityActionModel.getActivityTo();
                            List<ActivityFieldModel> activityFieldModels = activityFieldStore.findActivityFieldsByActivityId(activityTo.getId());
                            activityTo.setFields(activityFieldModels);
                        }

                        List<ActivityAction> activityActions = factory.convertTo(ActivityAction.class, activityActionModels);
                        activity.setActivityActions(activityActions);

                        tasks.add(task);
                    }
                }
                response = new ServiceResponse<Task>(tasks, "Read task's workflow for Entity: '" + entityModel.getLookup() + "' with ID: " + recordId, true);
            } else {
                response = new ServiceResponse<Task>("Not any processes found for Entity: " + entityModel.getLookup(), false);
            }
        } else {
            response = new ServiceResponse<Task>("Couldn't find Entity by lookup: " + entityLookup, false);
        }
        return response;
    }

}
