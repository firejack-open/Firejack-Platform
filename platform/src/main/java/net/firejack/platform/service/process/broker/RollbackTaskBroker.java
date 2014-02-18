package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Activity;
import net.firejack.platform.api.process.domain.Case;
import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.api.process.domain.TaskOperationsParams;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.exception.NoPreviousActivityException;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.model.registry.process.ActivityModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of task rollback
 */
@TrackDetails
@Component("rollbackTaskBroker")
public class RollbackTaskBroker extends ServiceBroker<ServiceRequest<TaskOperationsParams>, ServiceResponse<Task>> {

    @Autowired
    @Qualifier("taskStore")
    protected ITaskStore taskStore;

    @Autowired
    private TaskCaseProcessor taskCaseProcessor;

    /**
     * Invokes data access layer in order to rollback the task.
     * It is obligatory for the task to be active and for the currently logged in user and selected assignee to be in the actor set.
     * In addition, tehre has to be a previous activity.
     *
     * @param request service request containing note, ID and description of the task and IDs of assignee and explanation
     * @return service response containing info about the success of the rollback
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<Task> perform(ServiceRequest<TaskOperationsParams> request) throws Exception {
        ServiceResponse<Task> response;
        try {
            TaskOperationsParams data = request.getData();
            TaskModel taskModel = taskStore.findById(data.getTaskId());
            if (taskModel == null) {
                response = new ServiceResponse<Task>("No such task was found for the specified taskId.", false);
            } else {
                OPFContext context;
                try {
                    context = OPFContext.getContext();
                } catch (ContextLookupException e) {
                    context = null;
                }
                if (context == null || context.getPrincipal().isGuestPrincipal()) {
                    response = new ServiceResponse<Task>(
                            "Current user is not authorized to perform rollback for the task", false);
                } else {
                    ProcessModel processModel = taskModel.getCase().getProcess();
                    Boolean processSupportsMultiBranches = taskCaseProcessor.getMultiBranchStrategy(processModel.getLookup());
                    TaskModel previousTaskModel;
                    if (processSupportsMultiBranches && data.getActivityId() != null) {
                        previousTaskModel = taskStore.rollback(
                                data.getTaskId(), data.getAssigneeId(), data.getExplanationId(),
                                data.getNoteText(), data.getTaskDescription(), data.getActivityId());
                    } else {
                        previousTaskModel = taskStore.rollback(
                                data.getTaskId(), data.getAssigneeId(), data.getExplanationId(),
                                data.getNoteText(), data.getTaskDescription(), true);
                    }
                    Task task = convertCompactly(previousTaskModel);
                    response = new ServiceResponse<Task>(task, "Task rollback successful.", true);
                }
            }
        } catch (NoPreviousActivityException e) {
            response = new ServiceResponse<Task>("Error in task rollback: There is no previous activity.", false);
        } catch (TaskNotActiveException e) {
            response = new ServiceResponse<Task>("Task is not active. Cannot rollback.", false);
        } catch (UserNotInActorSetException e) {
            response = new ServiceResponse<Task>("Current user or selected previous assignee is not in the actor list.", false);
        }
        return response;
    }

    private Task convertCompactly(TaskModel taskModel) {
        Task task;
        if (taskModel == null) {
            task = null;
        } else {
            task = new Task();
            task.setId(taskModel.getId());
            task.setDescription(taskModel.getDescription());
            ActivityModel activityModel = taskModel.getActivity();
            Activity activity = new Activity();
            activity.setId(activityModel.getId());
            activity.setActivityType(activityModel.getActivityType());
            activity.setName(activityModel.getName());
            task.setActivity(activity);
            Case processCase = new Case();
            processCase.setId(taskModel.getCase().getId());
            task.setProcessCase(processCase);
        }
        return task;
    }
    
}
