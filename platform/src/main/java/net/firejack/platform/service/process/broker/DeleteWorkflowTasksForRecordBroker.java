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

import net.firejack.platform.api.process.domain.Task;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("deleteWorkflowTasksForRecordBroker")
public class DeleteWorkflowTasksForRecordBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Task>> {

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IProcessStore processStore;
    @Autowired
    private ITaskStore taskStore;

    @Override
    protected ServiceResponse<Task> perform(ServiceRequest<NamedValues> request) throws Exception {
        Long recordId = (Long) request.getData().get("recordId");
        Long entityId = (Long) request.getData().get("entityId");

        ServiceResponse<Task> response;

        EntityModel entityModel = entityStore.findById(entityId);
        if (entityModel != null) {
            List<ProcessModel> processModels = processStore.findAllByMainId(entityId);
            if (processModels.size() > 0) {
                for (ProcessModel processModel : processModels) {
                    taskStore.deleteWorkflowTask(processModel, entityModel, recordId);
                }
                response = new ServiceResponse<Task>("Workflow has been deleted for Entity: '" + entityModel.getLookup() + "' with ID: " + recordId, false);
            } else {
                response = new ServiceResponse<Task>("Not any processes found for Entity: " + entityModel.getLookup(), false);
            }
        } else {
            response = new ServiceResponse<Task>("Couldn't find Entity by id: " + entityId, false);
        }
        return response;
    }

}
