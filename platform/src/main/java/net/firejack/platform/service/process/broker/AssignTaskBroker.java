package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.TaskOperationsParams;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.model.service.mail.ITaskMailService;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the functionality of assigning a user to a task
 */
@TrackDetails
@Component("assignTaskBroker")
public class AssignTaskBroker extends ServiceBroker<ServiceRequest<TaskOperationsParams>, ServiceResponse> {

    @Autowired
    @Qualifier("taskStore")
    protected ITaskStore taskStore;

    @Autowired
    @Qualifier("taskMailService")
    private ITaskMailService taskMailService;

    /**
     * Assigns user to the task, recording the explanation and note. In addition, and an e-mail notification is sent.
     * the user has to be in the actor set and the task has to be active.
     *
     * @param request service request containing parameters for the assignment: IDs of the task, assignee and explanation and the content of the note
     * @return service response containing information about the success of the assignment
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse perform(ServiceRequest<TaskOperationsParams> request) throws Exception {

        Long taskId = request.getData().getTaskId();
        Long assigneeId = request.getData().getAssigneeId();
        Long explanationId = request.getData().getExplanationId();
        String noteText = request.getData().getNoteText();

        try {
            TaskModel taskModel = taskStore.assign(taskId, assigneeId, explanationId, noteText);
            taskMailService.sendEmailUserAssigned(taskModel);
        } catch (UserNotInActorSetException e) {
            return new ServiceResponse("Error assigning the task: The selected user is not in the actor set for the task.", false);
        } catch (TaskNotActiveException e) {
            return new ServiceResponse("Task is not active. Cannot assign.", false);
        }
        return new ServiceResponse("Task assigned successfully.", true);
    }
    
}
