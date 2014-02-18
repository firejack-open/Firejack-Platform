package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseOperationsParams;
import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.exception.NoPreviousActivityException;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of case rollback by ID
 */
@TrackDetails
@Component("rollbackCaseByIdBroker")
public class RollbackCaseByIdBroker extends ServiceBroker<ServiceRequest<CaseOperationsParams>, ServiceResponse> {

    @Autowired
    @Qualifier("taskStore")
    protected ITaskStore taskStore;

    @Autowired
    @Qualifier("caseStore")
    protected ICaseStore caseStore;

    @Autowired
    private TaskCaseProcessor taskCaseProcessor;

    /**
     * Invokes data access layer in order to find the case by its' ID and rollback its' active task.
     * It is necessary to have active task and previous activity.
     * In addition, the currently logged in user must be in the actor set for the case.
     *
     * @param request service request containing note, ID and description of the task and IDs of the assignee and explanation
     * @return service response containing the info about the success of the rollback
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse perform(ServiceRequest<CaseOperationsParams> request) throws Exception {
        CaseOperationsParams data = request.getData();
        CaseModel caseModel = caseStore.findByIdWithTasks(data.getCaseId());
        List<TaskModel> taskModels = caseModel.getTaskModels();
        TaskModel taskModel = null;
        for (TaskModel task : taskModels) {
            if (task.getActive()) {
                taskModel = task;
            }
        }
        ServiceResponse response;
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
                try {
                    ProcessModel processModel = taskModel.getCase().getProcess();
                    Boolean processSupportsMultiBranches = taskCaseProcessor.getMultiBranchStrategy(processModel.getLookup());
                    if (processSupportsMultiBranches && data.getActivityId() != null) {
                        taskStore.rollback(taskModel.getId(), data.getAssigneeId(),
                                data.getExplanationId(), data.getNoteText(), data.getTaskDescription(), data.getActivityId());
                    } else {
                        taskStore.rollback(taskModel.getId(), data.getAssigneeId(),
                                data.getExplanationId(), data.getNoteText(), data.getTaskDescription(), true);
                    }
                    response = new ServiceResponse<Task>("Rollback for the task performed successfully.", true);
                } catch (NoPreviousActivityException e) {
                    response = new ServiceResponse("Error in task rollback: There is no previous activity.", false);
                } catch (TaskNotActiveException e) {
                    response = new ServiceResponse("Task is not active. Cannot rollback.", false);
                } catch (UserNotInActorSetException e) {
                    response = new ServiceResponse("Current user or selected previous assignee is not in the actor list.", false);
                }
            }
        }
        return response;
    }

}
