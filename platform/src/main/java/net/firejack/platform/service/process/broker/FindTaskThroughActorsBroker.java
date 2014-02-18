package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class encapsulates the functionality of finding the tasks by actor
 */
@TrackDetails
@Component("findTaskThroughActorsBroker")
public class FindTaskThroughActorsBroker extends ListBroker<TaskModel, Task, NamedValues> {

    @Autowired
    @Qualifier("taskStore")
    private ITaskStore taskStore;

    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;

    @Autowired
    private TaskCaseProcessor taskCaseProcessor;

    /**
     * Invokes data access layer in order to find tasks by ID of the currently logged in user and actor lookup
     * @param namedValuesServiceRequest service request containing actor lookup, paging info
     * and flag showing whether the cases that are being searched for are active
     * @return list of found tasks
     * @throws BusinessFunctionException
     */
    @Override
    @SuppressWarnings("unchecked")
    protected List<TaskModel> getModelList(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        Long userId = ContextManager.getUserInfoProvider().getId();
        Integer offset = (Integer) namedValuesServiceRequest.getData().get("offset");
        Integer limit = (Integer) namedValuesServiceRequest.getData().get("limit");
        String sortColumn = TaskSearchTermVO.processSortColumn((String) namedValuesServiceRequest.getData().get("sort"));
        String sortDirection = (String) namedValuesServiceRequest.getData().get("dir");
        String lookupPrefix = (String) namedValuesServiceRequest.getData().get("lookupPrefix");
        Boolean active = (Boolean) namedValuesServiceRequest.getData().get("active");
        return taskStore.findAllByUserId(userId, lookupPrefix, active, getFilter(), offset, limit, sortColumn, sortDirection);
    }

    /**
     * Gets total count of found tasks
     * @param namedValuesServiceRequest service request containing lookup prefix
     * and flag showing whether the cases that are being searched for are active
     * @return number of found tasks
     * @throws BusinessFunctionException
     */
    @Override
    protected Integer getTotal(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        Long userId = ContextManager.getUserInfoProvider().getId();
        String lookupPrefix = (String) namedValuesServiceRequest.getData().get("lookupPrefix");
        Boolean active = (Boolean) namedValuesServiceRequest.getData().get("active");
        return (int)taskStore.countByUserId(userId, lookupPrefix, active, getFilter());
    }

    /**
     * Converts list of task entities to the list of task data transfer objects
     * @param entities list of task entities
     * @return list of task data transfer objects
     */
    @Override
    protected List<Task> convertToDTOs(List<TaskModel> entities) {
        List<ActorModel> actorsOfUser = actorStore.getActorsOfUser(ContextManager.getUserInfoProvider().getId());
        Set<Long> actorIds = new HashSet<Long>();
        for (ActorModel actor : actorsOfUser) {
            actorIds.add(actor.getId());
        }
        List<Task> tasks = factory.convertTo(Task.class, entities);
        Map<String, List<Process>> processesMap = new HashMap<String, List<Process>>();
        for (Task task : tasks) {
            boolean userCanPerform = actorIds.contains(task.getActivity().getActor().getId());
            task.setUserCanPerform(userCanPerform);
            if (task.getProcessCase() != null && task.getActivity() != null) {
                task.getProcessCase().setHasPreviousTask(task.getActivity().getSortPosition() > 1);
                taskCaseProcessor.registerTaskProcess(task, processesMap);
            }
        }
        taskCaseProcessor.initializeCaseStrategy(processesMap);
        return tasks;
    }
    
}
