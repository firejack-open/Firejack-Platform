/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.service.process.broker;

import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.process.domain.SearchTaskCaseFilter;
import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.api.process.domain.TaskSearchTermVO;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.SortOrder;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Class encapsulates the functionality of searching for the tasks
 */
@TrackDetails
@Component("searchTaskListBroker")
public class SearchTaskListBroker extends ListBroker<TaskModel, Task, NamedValues> {

    @Autowired
    @Qualifier("taskStore")
    private ITaskStore taskStore;

    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;

    @Autowired
    private TaskCaseProcessor taskCaseProcessor;

    /**
     * Invokes data access layer in order to search the tasks by search parameters
     * @param namedValuesServiceRequest service request containing search parameters
     * @return list of found tasks
     * @throws BusinessFunctionException
     */
    @Override
    protected List<TaskModel> getModelList(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        TaskSearchTermVO taskSearchTermVO = getTaskSearchTermVO(namedValuesServiceRequest);
        return taskStore.findAllBySearchParams(taskSearchTermVO, getFilter());
    }

    /**
     * Gets total count for the found tasks
     * @param namedValuesServiceRequest service request containing search parameters
     * @return total number of found tasks
     * @throws BusinessFunctionException
     */
    @Override
    protected Integer getTotal(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        TaskSearchTermVO taskSearchTermVO = getTaskSearchTermVO(namedValuesServiceRequest);
        long l = taskStore.countAllBySearchParams(taskSearchTermVO, getFilter());
        return (int) l;
    }

    /**
     * Constructs search value object from the search parameters
     * @param namedValuesServiceRequest service request containing search parameters
     * @return search value object to be passes to data access layer
     */
    private TaskSearchTermVO getTaskSearchTermVO(ServiceRequest<NamedValues> namedValuesServiceRequest) {
        SearchTaskCaseFilter searchTaskCaseFilter = (SearchTaskCaseFilter) namedValuesServiceRequest.getData().get("searchFilter");
        Paging paging = (Paging) namedValuesServiceRequest.getData().get("paging");

        TaskSearchTermVO taskSearchTermVO = new TaskSearchTermVO();
        taskSearchTermVO.setActive(searchTaskCaseFilter.getActive());
        taskSearchTermVO.setActivityId(searchTaskCaseFilter.getActivityId());
        taskSearchTermVO.setAssigneeId(searchTaskCaseFilter.getAssigneeId());
        taskSearchTermVO.setDescription(searchTaskCaseFilter.getDescription());
        taskSearchTermVO.setEndDate(searchTaskCaseFilter.getEndDate());
        taskSearchTermVO.setLookupPrefix(searchTaskCaseFilter.getLookupPrefix());
        taskSearchTermVO.setProcessId(searchTaskCaseFilter.getProcessId());
        taskSearchTermVO.setStartDate(searchTaskCaseFilter.getStartDate());
        taskSearchTermVO.setStatusId(searchTaskCaseFilter.getStatusId());

        taskSearchTermVO.setLimit(paging.getLimit());
        taskSearchTermVO.setOffset(paging.getOffset());
        SortOrder sortDirection = paging.getSortDirection();
        taskSearchTermVO.setSortDirection(sortDirection != null ? sortDirection.name() : SortOrder.ASC.name());
        taskSearchTermVO.setSortColumn(paging.getSortColumn());

        return taskSearchTermVO;
    }

    /**
     * Converts task entities to the task data transfer object.
     * In addition, the information about the user being able to perform it is set to each task data transfer object.
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
