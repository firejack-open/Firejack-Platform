package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class encapsulates the functionality of finding tasks by user actor
 */
@TrackDetails
@Component("findTaskBelongingToUserActorBroker")
public class FindTaskBelongingToUserActorBroker extends ListBroker<TaskModel, Task, NamedValues> {

    @Autowired
    @Qualifier("taskStore")
    private ITaskStore taskStore;

    @Autowired
    private TaskCaseProcessor taskCaseProcessor;

    /**
     * Invokes data access layer on order to find the tasks
     * @param namedValuesServiceRequest service request containing lookup prefix and paging info
     * @return list of found tasks
     * @throws BusinessFunctionException
     */
    @Override
    protected List<TaskModel> getModelList(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        Long userId = ContextManager.getUserInfoProvider().getId();
        Integer offset = (Integer) namedValuesServiceRequest.getData().get("offset");
        Integer limit = (Integer) namedValuesServiceRequest.getData().get("limit");
        String sortColumn = TaskSearchTermVO.processSortColumn((String) namedValuesServiceRequest.getData().get("sort"));
        String sortDirection = (String) namedValuesServiceRequest.getData().get("dir");
        String lookupPrefix = (String) namedValuesServiceRequest.getData().get("lookupPrefix");
        return taskStore.findBelongingToUserActor(userId, lookupPrefix, getFilter(), offset, limit, sortColumn, sortDirection);
    }

    /**
     * Gets total count of found tasks
     * @param namedValuesServiceRequest service request containing lookup prefix
     * @return number of found tasks
     * @throws BusinessFunctionException
     */
    @Override
    protected Integer getTotal(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        Long userId = ContextManager.getUserInfoProvider().getId();
        String lookupPrefix = (String) namedValuesServiceRequest.getData().get("lookupPrefix");
        return (int)taskStore.countBelongingToUserActor(userId, lookupPrefix, getFilter());
    }

    /**
     * Converts list of task entities to the list of task data transfer objects
     * @param entities list of task entities to be converted
     * @return list of task data transfer objects
     */
    @Override
    protected List<Task> convertToDTOs(List<TaskModel> entities) {
        List<Task> tasks = factory.convertTo(Task.class, entities);
        Map<String, List<Process>> processesMap = new HashMap<String, List<Process>>();
        for (Task task : tasks) {
            task.setUserCanPerform(true); // task is found by belonging to user's actor - always can perform
            if (task.getProcessCase() != null && task.getActivity() != null) {
                task.getProcessCase().setHasPreviousTask(task.getActivity().getSortPosition() > 1);
                taskCaseProcessor.registerTaskProcess(task, processesMap);
            }
        }
        taskCaseProcessor.initializeCaseStrategy(processesMap);
        return tasks;
    }
    
}
