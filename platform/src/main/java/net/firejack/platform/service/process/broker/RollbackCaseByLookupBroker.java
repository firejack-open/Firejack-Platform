package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseOperationsParams;
import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.exception.NoPreviousActivityException;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ICaseObjectStore;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Class encapsulates the functionality of case rollback by lookup
 */
@TrackDetails
@Component("rollbackCaseByLookupBroker")
public class RollbackCaseByLookupBroker extends ServiceBroker<ServiceRequest<CaseOperationsParams>, ServiceResponse> {

    @Autowired
    @Qualifier("taskStore")
    protected ITaskStore taskStore;

    @Autowired
    @Qualifier("caseObjectStore")
    protected ICaseObjectStore caseObjectStore;

    /**
     * Invokes data access layer on order to find the cases by process and entities
     * and to perform active task for each of them including the following system activities.
     * There has to be active task and previous activity for a case.
     * In addition, the currently logged in user has to be in the actor set.
     *
     * @param request service request containing process lookup, entities type and IDs, note, IDs of the assignee and explanation
     * @return service response containing the information about the success of the rollback
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse perform(ServiceRequest<CaseOperationsParams> request) throws Exception {
        CaseOperationsParams data = request.getData();
        List<Tuple<Long,CaseModel>> entityIdCases =
                caseObjectStore.findCaseByProcessAndEntity(
                        data.getProcessLookup(), data.getEntityIds(), data.getEntityType());

        ServiceResponse response = null;

        List<Long> taskIdList = new ArrayList<Long>();
        for (Tuple<Long, CaseModel> entityIdCase : entityIdCases) {
            CaseModel caseModel = entityIdCase.getValue();

            List<TaskModel> taskModels = caseModel.getTaskModels();
            TaskModel taskModel = null;
            for (TaskModel task : taskModels) {
                if (task.getActive()) {
                    taskModel = task;
                    break;
                }
            }

            if (taskModel == null) {
                String message = "No active tasks found for the case to rollback. CaseModel ID: " + caseModel.getId();
                logger.error(message);
                response = new ServiceResponse(message, false);
                break;
            } else {
                taskIdList.add(taskModel.getId());
            }
        }
        if (response == null) {
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
                    taskStore.rollbackIncludingFollowingSystemActivity(
                            taskIdList, data.getAssigneeId(), data.getExplanationId(),
                            data.getNoteText(), null, true);
                    response = new ServiceResponse("Rollback performed successfully.", false);
                } catch (NoPreviousActivityException e) {
                    logger.error(e.getMessage(), e);
                    response = new ServiceResponse(
                            "There is no previous activity found for the specified task information.", false);
                } catch (TaskNotActiveException e) {
                    logger.error(e.getMessage(), e);
                    response = new ServiceResponse("Failed to rollback - task is not active.", false);
                } catch (UserNotInActorSetException e) {
                    logger.error(e.getMessage(), e);
                    response = new ServiceResponse("Current user or selected next assignee is not in the actor list.", false);
                }
            }
        }

        return response;
    }
    
}
