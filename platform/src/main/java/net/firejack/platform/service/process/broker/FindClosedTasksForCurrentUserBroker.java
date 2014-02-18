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
 * Class encapsulates the functionality of finding closed tasks for currently logged in user
 */
@TrackDetails
@Component("findClosedTasksForCurrentUserBroker")
public class FindClosedTasksForCurrentUserBroker extends ListBroker<TaskModel, Task, NamedValues> {

    @Autowired
    @Qualifier("taskStore")
    private ITaskStore taskStore;

    @Autowired
    private TaskCaseProcessor taskCaseProcessor;

    /**
     * Invokes data access layer in order to find closed tasks for currently logged in user
     * @param namedValuesServiceRequest service request containing lookup prefix and paging info
     * @return list of found tasks
     * @throws BusinessFunctionException
     */
    @Override
    protected List<TaskModel> getModelList(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        Long userId = ContextManager.getUserInfoProvider().getId();
        Integer offset = (Integer) namedValuesServiceRequest.getData().get("offset");
        Integer limit = (Integer) namedValuesServiceRequest.getData().get("limit");
        String sortColumn = TaskSearchTermVO.processSortColumn(
                (String) namedValuesServiceRequest.getData().get("sort"));
        String sortDirection = (String) namedValuesServiceRequest.getData().get("dir");
        String lookupPrefix = (String) namedValuesServiceRequest.getData().get("lookupPrefix");
        return taskStore.findClosedTasksForCurrentUser(userId, lookupPrefix, getFilter(), offset, limit, sortColumn, sortDirection);
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
        return (int)taskStore.countClosedTasksForCurrentUser(userId, lookupPrefix, getFilter());
    }

    @Override
    protected List<Task> convertToDTOs(List<TaskModel> entities) {
        List<Task> tasks = super.convertToDTOs(entities);
        Map<String, List<Process>> processesMap = new HashMap<String, List<Process>>();
        for (Task task : tasks) {
            if (task.getProcessCase() != null && task.getActivity() != null) {
                task.getProcessCase().setHasPreviousTask(task.getActivity().getSortPosition() > 1);
                taskCaseProcessor.registerTaskProcess(task, processesMap);
            }
        }
        taskCaseProcessor.initializeCaseStrategy(processesMap);
        return tasks;
    }
}
