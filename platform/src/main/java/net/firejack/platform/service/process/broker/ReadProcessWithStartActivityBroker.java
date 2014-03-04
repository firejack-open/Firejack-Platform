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
import net.firejack.platform.api.process.domain.ActivityOrder;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.process.ActivityFieldModel;
import net.firejack.platform.core.model.registry.process.ActivityModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.IActivityStore;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.generate.tools.Utils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Class encapsulates the functionality of retrieving a case
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readProcessWithStartActivityBroker")
public class ReadProcessWithStartActivityBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Process>> {

    @Autowired
    private IProcessStore processStore;
    @Autowired
    private IActivityStore activityStore;

    @Override
    protected ServiceResponse<Process> perform(ServiceRequest<NamedValues> request) throws Exception {
        String processLookup = (String) request.getData().get("processLookup");

        ServiceResponse<Process> response;

        ProcessModel processModel = processStore.findByLookup(processLookup);
        if (processModel != null) {
            ActivityModel startActivityModel = null;
            List<ActivityModel> activityModels = activityStore.findByProcessId(processModel.getId(), null);
            for (ActivityModel activityModel : activityModels) {
                if (ActivityOrder.START.equals(activityModel.getActivityOrder())) {
                    startActivityModel = activityModel;
                    break;
                }
            }
            if (startActivityModel != null) {
                for (ActivityFieldModel activityFieldModel : startActivityModel.getFields()) {
                    activityFieldModel.setActivity(null);
                    FieldModel field = activityFieldModel.getField();
                    String formattedFieldName = Utils.fieldModelFormatting(field.getName());
                    field.setName(formattedFieldName);
                }
                Activity startActivity = factory.convertTo(Activity.class, startActivityModel);

                Process process = factory.convertTo(Process.class, processModel);
                process.setExplanations(null);
                process.setEntity(null);
                process.setProcessFields(null);
                process.setStatuses(null);
                process.setParameters(null);
                process.setActivities(Arrays.asList(null, startActivity));

                response = new ServiceResponse<Process>(process, "Start Activity has been found successfully", true);
            } else {
                response = new ServiceResponse<Process>("Could not find Start Activity for Process: " + processLookup, false);
            }
        } else {
            response = new ServiceResponse<Process>("Could not find Process by: " + processLookup, false);
        }
        return response;
    }
}
