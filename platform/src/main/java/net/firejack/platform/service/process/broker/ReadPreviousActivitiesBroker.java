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

import net.firejack.platform.api.process.domain.Activity;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.process.ActivityModel;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.IActivityStore;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;


/**
 * Class encapsulates functionality of retrieving the previous activities by specified taskId or caseId
 */
@TrackDetails
@Component
public class ReadPreviousActivitiesBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Long>>, ServiceResponse<Activity>> {

    public static final String PARAM_TASK_ID = "taskId";
    public static final String PARAM_CASE_ID = "caseId";

    @Autowired
    @Qualifier("caseStore")
    private ICaseStore caseStore;

    @Autowired
    @Qualifier("taskStore")
    private ITaskStore taskStore;

    @Autowired
    @Qualifier("activityStore")
    private IActivityStore store;

    @Autowired
    private TaskCaseProcessor taskCaseProcessor;

    @Override
    protected ServiceResponse<Activity> perform(ServiceRequest<NamedValues<Long>> request)
            throws Exception {
        Long caseId = request.getData().get(PARAM_CASE_ID);
        Long taskId = request.getData().get(PARAM_TASK_ID);
        ServiceResponse<Activity> response;
        if (caseId == null && taskId == null) {
            response = new ServiceResponse<Activity>("taskId or caseId parameter should be specified.", false);
        } else {
            Integer currentActivityPosition = null;
            ProcessModel process = null;
            if (taskId == null) {
                CaseModel caseModel = caseStore.findById(caseId);
                if (caseModel != null) {
                    currentActivityPosition = caseModel.getStatus().getSortPosition();
                    process = caseModel.getProcess();
                }
            } else {
                TaskModel taskModel = taskStore.findById(taskId);
                if (taskModel != null) {
                    currentActivityPosition = taskModel.getActivity().getSortPosition();
                    process = taskModel.getCase().getProcess();
                }
            }
            if (currentActivityPosition == null || process == null) {
                response = new ServiceResponse<Activity>("Wrong taskId or caseId parameter value.", false);
            } else {
                LinkedList<Criterion> criterionList = new LinkedList<Criterion>();
                criterionList.add(Restrictions.and(
                        Restrictions.lt("sortPosition", currentActivityPosition),
                        Restrictions.eq("parent.id", process.getId())
                ));
                List<ActivityModel> previousActivities = store.search(criterionList, null);
                if (previousActivities == null || previousActivities.isEmpty()) {
                    response = new ServiceResponse<Activity>("No previous activities are available", true);
                } else {
                    Boolean multiBranchStrategySupported = taskCaseProcessor.getMultiBranchStrategy(process.getLookup());
                    if (Boolean.TRUE.equals(multiBranchStrategySupported)) {
                        List<Activity> activities = factory.convertTo(Activity.class, previousActivities);
                        response = new ServiceResponse<Activity>(activities, "Success", true);
                    } else {
                        Activity activity = factory.convertTo(Activity.class, previousActivities.get(0));
                        response = new ServiceResponse<Activity>(activity, "Success", true);
                    }
                }
            }
        }
        return response;
    }

}