package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseOperationsParams;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.exception.TaskNotActiveException;
import net.firejack.platform.core.exception.UserNotInActorSetException;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ICaseObjectStore;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of performing a case by its' lookup
 */
@TrackDetails
@Component("performCaseByLookupBroker")
public class PerformCaseByLookupBroker extends ServiceBroker<ServiceRequest<CaseOperationsParams>, ServiceResponse> {

    @Autowired
    @Qualifier("caseStore")
    private ICaseStore caseStore;

    @Autowired
    @Qualifier("caseObjectStore")
    protected ICaseObjectStore caseObjectStore;

    @Autowired
    @Qualifier("taskStore")
    private ITaskStore taskStore;

    /**
     * Invokes data access layer in order to find the case by process and entity lookups and entity IDs
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
        CaseOperationsParams caseOperationsParams = request.getData();

        String processLookup = caseOperationsParams.getProcessLookup();
        List<Long> entityIds = caseOperationsParams.getEntityIds();
        String entityLookup = caseOperationsParams.getEntityType();

        List<Tuple<Long,CaseModel>> entityIdCases = caseObjectStore.findCaseByProcessAndEntity(processLookup, entityIds, entityLookup);

        Long assigneeId = caseOperationsParams.getAssigneeId();
        Long explanationId = caseOperationsParams.getExplanationId();
        String noteText = caseOperationsParams.getNoteText();


        boolean allCasesPerformed = true;

        for (Tuple<Long, CaseModel> entityIdCase : entityIdCases) {
            CaseModel processCase = entityIdCase.getValue();

            List<TaskModel> taskModels = processCase.getTaskModels();
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
                taskStore.performIncludingFollowingSystemActivities(taskId, assigneeId, explanationId, noteText, null);
            } catch (TaskNotActiveException e) {
                logger.error("No active tasks for the case to perform. CaseModel ID: " + processCase.getId());
                allCasesPerformed = false;
            } catch (UserNotInActorSetException e) {
                logger.error("Current user or selected next assignee is not in the actor list. CaseModel ID: " + processCase.getId());
                allCasesPerformed = false;
            }
        }

        return new ServiceResponse(allCasesPerformed ? "All cases performed successfully" : "Some of the cases failed to perform.", allCasesPerformed);
    }
    
}
