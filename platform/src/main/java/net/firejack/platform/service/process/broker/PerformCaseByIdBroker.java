package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseOperationsParams;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.model.principal.OpenFlamePrincipal;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of performing a case by its' ID
 */
@TrackDetails
@Component("performCaseByIdBroker")
public class PerformCaseByIdBroker extends ServiceBroker<ServiceRequest<CaseOperationsParams>, ServiceResponse> {

    @Autowired
    @Qualifier("caseStore")
    private ICaseStore caseStore;

    @Autowired
    @Qualifier("taskStore")
    private ITaskStore taskStore;

    /**
     * Invokes data access layer in order to find the case by ID
     * and perform its' currently active task along with the following system activities.
     * It's necessary to have the active task.
     * In addition, currently logged in user as well as the selected assignee have to be in the actor set for the case.
     *
     * @param request service request containing parameter for performing the case
     * @return service response containing information about the success of the operation
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

        ServiceResponse response;
        try {
            if (taskId == null) {
                throw new TaskNotActiveException();
            }
            OpenFlamePrincipal openFlamePrincipal = OPFContext.getContext().getPrincipal();
            if (openFlamePrincipal.isGuestPrincipal()) {
                response = new ServiceResponse("Current user is not in the actor list.", false);
            } else {
                taskStore.performIncludingFollowingSystemActivities(taskId, request.getData().getAssigneeId(), request.getData().getExplanationId(), request.getData().getNoteText(), request.getData().getTaskDescription());
                response = new ServiceResponse("Case performed successfully.", true);
            }
        } catch (TaskNotActiveException e) {
            response = new ServiceResponse("Task is not active. Cannot perform.", false);
        } catch (UserNotInActorSetException e) {
            response = new ServiceResponse("Current user or selected next assignee is not in the actor list.", false);
        }

        return response;
    }
    
}
