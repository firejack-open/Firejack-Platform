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

import net.firejack.platform.api.process.domain.*;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ActivityActionModel;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.model.registry.process.CaseObjectModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.IActivityActionStore;
import net.firejack.platform.core.store.process.ICaseObjectStore;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.QueryOperation;
import net.firejack.platform.core.utils.SearchQuery;
import net.firejack.platform.core.utils.SortField;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Class encapsulates the functionality of searching for the tasks
 */
@TrackDetails
@Component("advancedSearchTaskBroker")
public class AdvancedSearchTaskBroker extends ListBroker<TaskModel, Task, NamedValues> {

    @Autowired
    @Qualifier("taskStore")
    private ITaskStore taskStore;
    @Autowired
    @Qualifier("actorStore")
    private IActorStore actorStore;
    @Autowired
    private TaskCaseProcessor taskCaseProcessor;
    @Autowired
    private IActivityActionStore activityActionStore;
    @Autowired
    private ICaseObjectStore caseObjectStore;


    @Override
    protected List<TaskModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
        String queryParameters = (String) request.getData().get("queryParameters");
        Integer offset = (Integer) request.getData().get("offset");
        Integer limit = (Integer) request.getData().get("limit");
        String sortOrders = (String) request.getData().get("sortOrders");
        String type = (String) request.getData().get("type");

        try {
            List<SortField> sortFields = WebUtils.deserializeJSON(sortOrders, List.class, SortField.class);
            List<List<SearchQuery>> searchQueries = WebUtils.deserializeJSON(queryParameters, List.class, List.class, SearchQuery.class);
            addTaskActiveQueryParameter(searchQueries);
            return taskStore.advancedSearch(type, searchQueries, new Paging(offset, limit, sortFields));
        } catch (IOException e) {
            throw new BusinessFunctionException(e.getMessage(), e);
        }
    }

    @Override
    protected Integer getTotal(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
        String queryParameters = (String) request.getData().get("queryParameters");
        String type = (String) request.getData().get("type");

        try {
            List<List<SearchQuery>> searchQueries = WebUtils.deserializeJSON(queryParameters, List.class, List.class, SearchQuery.class);
            addTaskActiveQueryParameter(searchQueries);
            return taskStore.advancedSearchCount(type, searchQueries);
        } catch (IOException e) {
            throw new BusinessFunctionException(e.getMessage(), e);
        }
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
        Map<Long, List<ActivityAction>> activityActionCache = new HashMap<Long, List<ActivityAction>>();
        for (Task task : tasks) {
            Activity activity = task.getActivity();
            boolean userCanPerform = actorIds.contains(activity.getActor().getId());
            task.setUserCanPerform(userCanPerform);
            if (task.getProcessCase() != null) {
                task.getProcessCase().setHasPreviousTask(activity.getSortPosition() > 1);
                taskCaseProcessor.registerTaskProcess(task, processesMap);
            }
            List<ActivityAction> activityActions = activityActionCache.get(activity.getId());
            if (activityActions == null) {
                List<ActivityActionModel> activityActionModels = activityActionStore.findActionsFromActivity(activity.getId());
                activityActions = factory.convertTo(ActivityAction.class, activityActionModels);
                activityActionCache.put(activity.getId(), activityActions);
            }
            activity.setActivityActions(activityActions);

            CaseObjectModel caseObjectModel = caseObjectStore.findByTask(task.getId());
            CaseObject caseObject = factory.convertTo(CaseObject.class, caseObjectModel);
            task.setCaseObject(caseObject);
        }
        taskCaseProcessor.initializeCaseStrategy(processesMap);
        return tasks;
    }

    private void addTaskActiveQueryParameter(List<List<SearchQuery>> searchQueriesList) {
        SearchQuery taskActiveSearchQuery = new SearchQuery("active", QueryOperation.EQUALS, true);
        if (searchQueriesList.size() == 0) {
            List<SearchQuery> searchQueries = new ArrayList<SearchQuery>();
            searchQueriesList.add(searchQueries);
        }
        for (List<SearchQuery> searchQueries : searchQueriesList) {
            searchQueries.add(taskActiveSearchQuery);
        }
    }

}
