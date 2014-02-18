package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class encapsulates the functionality of finding the cases by actor
 */
@TrackDetails
@Component("findCaseThroughActorsBroker")
public class FindCaseThroughActorsBroker extends ListBroker<CaseModel, Case, NamedValues> {

    @Autowired
    @Qualifier("caseStore")
    private ICaseStore caseStore;

    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;

    @Autowired
    private TaskCaseProcessor taskCaseProcessor;

    /**
     * Invokes data access layer in order to find cases by ID of the currently logged in user and actor lookup
     * @param namedValuesServiceRequest service request containing actor lookup, paging info
     * and flag showing whether the cases that are being searched for are active
     * @return list of found cases
     * @throws BusinessFunctionException
     */
    @Override
    protected List<CaseModel> getModelList(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        Long userId = ContextManager.getUserInfoProvider().getId();
        Integer offset = (Integer) namedValuesServiceRequest.getData().get("offset");
        Integer limit = (Integer) namedValuesServiceRequest.getData().get("limit");
        String sortColumn = CaseSearchTermVO.processSortColumn((String) namedValuesServiceRequest.getData().get("sort"));
        String sortDirection = (String) namedValuesServiceRequest.getData().get("dir");
        String lookupPrefix = (String) namedValuesServiceRequest.getData().get("lookupPrefix");
        Boolean active = (Boolean) namedValuesServiceRequest.getData().get("active");
        return caseStore.findAllByUserId(userId, lookupPrefix, active, getFilter(), offset, limit, sortColumn, sortDirection);
    }

    /**
     * Converts list of case entities to the list of case data transfer objects
     * @param entities list of case entities
     * @return list of case data transfer objects
     */
    @Override
    protected List<Case> convertToDTOs(List<CaseModel> entities) {
        List<ActorModel> actorsOfUser = actorStore.getActorsOfUser(ContextManager.getUserInfoProvider().getId());
        Set<Long> actorIds = new HashSet<Long>();
        for (ActorModel actorModel : actorsOfUser) {
            actorIds.add(actorModel.getId());
        }
        List<Case> cases = new ArrayList<Case>();
        Map<String, List<Process>> processesMap = new HashMap<String, List<Process>>();
        for (CaseModel entity : entities) {
            Case processCase = factory.convertTo(Case.class, entity);
            for (TaskModel taskModel : entity.getTaskModels()) {
                if (taskModel != null && taskModel.getActive()) { // tasks returned by LEFT OUTER JOIN, might be null if there are no active tasks
                    boolean hasPreviousTask = taskModel.getActivity().getSortPosition() > 1;
                    processCase.setHasPreviousTask(hasPreviousTask);
                    boolean userCanPerform = actorIds.contains(taskModel.getActivity().getActor().getId());
                    processCase.setUserCanPerform(userCanPerform);
                    break;
                }
            }
            taskCaseProcessor.registerCaseProcess(processCase, processesMap);
            cases.add(processCase);
        }
        taskCaseProcessor.initializeCaseStrategy(processesMap);
        return cases;
    }

    /**
     * Gets total count of found cases
     * @param namedValuesServiceRequest service request containing lookup prefix
     * and flag showing whether the cases that are being searched for are active
     * @return number of found cases
     * @throws BusinessFunctionException
     */
    @Override
    protected Integer getTotal(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        Long userId = ContextManager.getUserInfoProvider().getId();
        String lookupPrefix = (String) namedValuesServiceRequest.getData().get("lookupPrefix");
        Boolean active = (Boolean) namedValuesServiceRequest.getData().get("active");
        return (int)caseStore.countAllByUserId(userId, lookupPrefix, active, getFilter());
    }
    
}