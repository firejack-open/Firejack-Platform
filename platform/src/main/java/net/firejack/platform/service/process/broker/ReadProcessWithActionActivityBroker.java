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
import net.firejack.platform.api.process.domain.ActivityAction;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.api.process.domain.Status;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.process.ActivityActionModel;
import net.firejack.platform.core.model.registry.process.ActivityFieldModel;
import net.firejack.platform.core.model.registry.process.ActivityModel;
import net.firejack.platform.core.model.registry.process.StatusModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.IActivityActionStore;
import net.firejack.platform.core.store.process.IActivityStore;
import net.firejack.platform.core.store.process.IStatusStore;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.generate.tools.Utils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class encapsulates the functionality of retrieving a case
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readProcessWithActionActivityBroker")
public class ReadProcessWithActionActivityBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Process>> {

    @Autowired
    private IProcessStore processStore;
    @Autowired
    private IActivityStore activityStore;
    @Autowired
    private IStatusStore statusStore;
    @Autowired
    private IActivityActionStore activityActionStore;

    @Override
    protected ServiceResponse<Process> perform(ServiceRequest<NamedValues> request) throws Exception {
        Long activityActionId = (Long) request.getData().get("activityActionId");

        ServiceResponse<Process> response;

        ActivityActionModel activityActionModel = activityActionStore.findById(activityActionId);
        if (activityActionModel != null) {
            Long toActivityId = activityActionModel.getActivityTo().getId();
            Long fromActivityId = activityActionModel.getActivityFrom().getId();

            ActivityModel fromActivityModel = activityStore.findWithFieldsById(fromActivityId, null);
            ActivityModel toActivityModel = activityStore.findWithFieldsById(toActivityId, null);
            if (toActivityModel != null && fromActivityModel != null) {
                formatFieldNames(toActivityModel);
                Activity toActivity = factory.convertTo(Activity.class, toActivityModel);

                formatFieldNames(fromActivityModel);
                Activity fromActivity = factory.convertTo(Activity.class, fromActivityModel);

                if (activityActionModel.getStatus() != null && activityActionModel.getStatus().getId() != null) {
                    StatusModel statusModel = statusStore.findById(activityActionModel.getStatus().getId());
                    Status status = factory.convertTo(Status.class, statusModel);
                    fromActivity.setStatus(status);
                }

                ActivityAction activityAction = factory.convertTo(ActivityAction.class, activityActionModel);
                List<ActivityAction> activityActions = new ArrayList<ActivityAction>();
                activityActions.add(activityAction);
                toActivity.setActivityActions(activityActions);

                Process process = factory.convertTo(Process.class, toActivityModel.getParent());
                process.setExplanations(null);
                process.setEntity(null);
                process.setProcessFields(null);
                process.setStatuses(null);
                process.setParameters(null);
                process.setActivities(Arrays.asList(fromActivity, toActivity));

                response = new ServiceResponse<Process>(process, "To Activity has been found successfully", true);
            } else {
                response = new ServiceResponse<Process>("Could not find To Activity by ID: " + toActivityId, false);
            }

        } else {
            response = new ServiceResponse<Process>("Could not find Activity Action by ID: " + activityActionId, false);
        }
        return response;
    }

    private void formatFieldNames(ActivityModel toActivityModel) {
        for (ActivityFieldModel activityFieldModel : toActivityModel.getFields()) {
            activityFieldModel.setActivity(null);
            FieldModel field = activityFieldModel.getField();
            if (field != null) {
                String formattedFieldName = Utils.fieldModelFormatting(field.getName());
                field.setName(formattedFieldName);
            }
        }
    }
}
