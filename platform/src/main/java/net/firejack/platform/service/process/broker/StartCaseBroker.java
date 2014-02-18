package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.CaseObject;
import net.firejack.platform.api.process.domain.CaseOperationsParams;
import net.firejack.platform.api.process.domain.ProcessFieldCaseValue;
import net.firejack.platform.api.process.domain.UserActor;
import net.firejack.platform.api.process.domain.process.ProcessCustomFieldVO;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.process.CaseObjectModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class encapsulates the functionality of starting a case
 */
@TrackDetails
@Component("startCaseBroker")
public class StartCaseBroker extends ServiceBroker
        <ServiceRequest<CaseOperationsParams>, ServiceResponse<SimpleIdentifier<Long>>> {

    @Autowired
    @Qualifier("caseStore")
    private ICaseStore caseStore;

    @Autowired
    @Qualifier("userStore")
    private IUserStore userStore;

    /**
     * Invokes data access layer in order to start a case
     *
     * @param request service request containing list of user actor value objects, case objects,
     *                assignee ID, process lookup, case description and flag showing whether assignee is allowed to be null
     * @return service response containing the info about the success of the operation and the ID of the started case of successful
     * @throws net.firejack.platform.core.exception.BusinessFunctionException
     */
    @Override
    protected ServiceResponse<SimpleIdentifier<Long>> perform(ServiceRequest<CaseOperationsParams> request) throws Exception {
	    try {
            Map<Long, Set<Long>> userActorsMap;
            String checkResultMsg = null;
            if (request.getData().getUserActors() != null) {
                userActorsMap = new HashMap<Long, Set<Long>>();
                for (UserActor userActor : request.getData().getUserActors()) {
                    if (userActor.getUser().getId() == null) {
                        checkResultMsg = "Specified user actor does not have userId set.";
                        break;
                    } else if (userActor.getActor().getId() == null) {
                        checkResultMsg = "Specified user actor does not have actorId set.";
                        break;
                    } else {
                        Set<Long> actorSet = userActorsMap.get(userActor.getActor().getId());
                        if (actorSet == null) {
                            actorSet = new HashSet<Long>();
                            userActorsMap.put(userActor.getUser().getId(), actorSet);
                        }
                        actorSet.add(userActor.getActor().getId());
                    }
                }
            } else {
                userActorsMap = null;
            }

            if (checkResultMsg != null) {
                return new ServiceResponse<SimpleIdentifier<Long>>(checkResultMsg, false);
            }
            
            Map<String, List<ProcessCustomFieldVO>> customFields = new HashMap<String, List<ProcessCustomFieldVO>>();
            if (request.getData().getCaseObjects() != null) {
                for (CaseObject caseObject : request.getData().getCaseObjects()) {
                    List<ProcessCustomFieldVO> processCustomFieldVOs = new ArrayList<ProcessCustomFieldVO>();
                    if (caseObject.getProcessFields() != null) {
                        for (ProcessFieldCaseValue processFieldCaseValue : caseObject.getProcessFields()) {
                            ProcessCustomFieldVO processCustomFieldVO = ProcessCustomFieldVO.instantiate(
                                    processFieldCaseValue.getFieldLookup(), processFieldCaseValue.getValue());
                            processCustomFieldVOs.add(processCustomFieldVO);
                        }
                    }
                    customFields.put(caseObject.getEntityType(), processCustomFieldVOs);
                }
            }

            List<CaseObjectModel> caseObjectModels = factory.convertFrom(
                    CaseObjectModel.class, request.getData().getCaseObjects());

            UserModel assignee = null;
            Long assigneeId = request.getData().getAssigneeId();
            if (assigneeId != null) {
                assignee = userStore.findById(assigneeId);
            }
            UserModel currentUser = null;
            Long currentUserId = ContextManager.getUserInfoProvider().getId();
            if (currentUserId != null) {
                currentUser = userStore.findById(currentUserId);
            }
            Long caseId = caseStore.externalStart(request.getData().getProcessLookup(),
                    assignee, currentUser, caseObjectModels, userActorsMap, request.getData().getCaseDescription(),
                    request.getData().isAllowNullAssignee(), customFields);
            return new ServiceResponse<SimpleIdentifier<Long>>(
                    new SimpleIdentifier<Long>(caseId), "Case started successfully.", true);
        } catch (Exception e) {
            logger.error("Failed to start the case", e);
            return new ServiceResponse<SimpleIdentifier<Long>>("Failed to start the case. Reason: " + e.getMessage(), false);
        }
    }
    
}
