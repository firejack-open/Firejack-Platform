package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseOperationsParams;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.exception.NoPreviousActivityException;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates double rollback of case functionality
 */
@TrackDetails
@Component("doubleRollbackCaseBroker")
public class DoubleRollbackCaseBroker extends ServiceBroker<ServiceRequest<CaseOperationsParams>, ServiceResponse> {

    @Autowired
    @Qualifier("taskStore")
    protected ITaskStore taskStore;

    @Autowired
    @Qualifier("caseStore")
    protected ICaseStore caseStore;

    /**
     * Invokes the data access layer in order to rollback the case for 2 activities.
     * It's necessary to have previous activities ad active task.
     * In addition, the currently logged in user as well as the selected assignees have to be in the actor set for the case.
     *
     * @param request service request containing note text, task description and IDs of case, assignee and explanation
     * @return service response containing the information about the success of double rollback
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse perform(ServiceRequest<CaseOperationsParams> request) throws Exception {
        CaseModel caseModel = caseStore.findByIdWithTasks(request.getData().getCaseId());
        List<TaskModel> taskModels = caseModel.getTaskModels();
        Long taskId = null;
        for (TaskModel taskModel : taskModels) {
            if (taskModel.getActive()) {
                taskId = taskModel.getId();
            }
        }
         try {
            if (taskId == null) {
                throw new TaskNotActiveException();
            }
            TaskModel previousTaskModel = taskStore.rollback(
                    taskId, request.getData().getTaskId(), request.getData().getExplanationId(),
                    request.getData().getNoteText(), request.getData().getTaskDescription(), true);
            taskStore.rollback(
                    previousTaskModel.getId(), request.getData().getAssigneeId(), request.getData().getExplanationId(),
                    request.getData().getNoteText(), request.getData().getTaskDescription(), false);
        } catch (NoPreviousActivityException e) {
            return new ServiceResponse("Error in task rollback: There is no previous activity.", false);
        } catch (TaskNotActiveException e) {
            return new ServiceResponse("Task is not active. Cannot rollback.", false);
        } catch (UserNotInActorSetException e) {
            return new ServiceResponse("Current user or selected previous assignee is not in the actor list.", false);
        }
        return new ServiceResponse("Case rollback successful.", false);
    }
    
}
