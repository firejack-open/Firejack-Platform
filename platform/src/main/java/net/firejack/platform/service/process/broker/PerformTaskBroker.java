package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.api.process.domain.TaskOperationsParams;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of performing a task
 */
@TrackDetails
@Component("performTaskBroker")
public class PerformTaskBroker extends ServiceBroker<ServiceRequest<TaskOperationsParams>, ServiceResponse<Task>> {

    @Autowired
    @Qualifier("taskStore")
    protected ITaskStore taskStore;

    /**
     * Invokes the data access layer in order to perform the task along with the following system activities.
     * The next task entity is returned to the business layer and converted to the data transfer object to be incorporated in the method response.
     * It's obligatory for the task to be active and for the currently logged in user and the selected assignees to be in the actor set.
     *
     * @param request service request containing note, task description and IDs of the task, assignee and explanation
     * @return service response containing the information about the operation success and the next task if exists
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<Task> perform(ServiceRequest<TaskOperationsParams> request) throws Exception {
        Long taskId = request.getData().getTaskId();
        Long assigneeId = request.getData().getAssigneeId();
        Long explanationId = request.getData().getExplanationId();
        String noteText = request.getData().getNoteText();
        String taskDescription = request.getData().getTaskDescription();
        
        try {
            TaskModel nextTaskModel = taskStore.performIncludingFollowingSystemActivities(taskId, assigneeId, explanationId, noteText, taskDescription);
            Task nextTask = factory.convertTo(Task.class, nextTaskModel);
            return nextTask == null ?
                    new ServiceResponse<Task>("Task performed successfully.", true) :
                    new ServiceResponse<Task>(nextTask, "Task performed successfully.", true);
        } catch (TaskNotActiveException e) {
            return new ServiceResponse<Task>("Task is not active. Cannot perform.", false);
        } catch (UserNotInActorSetException e) {
            return new ServiceResponse<Task>("Current user or selected next assignee is not in the actor list.", false);
        }
    }
    
}
