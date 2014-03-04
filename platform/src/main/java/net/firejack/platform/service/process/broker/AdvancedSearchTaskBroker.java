/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
