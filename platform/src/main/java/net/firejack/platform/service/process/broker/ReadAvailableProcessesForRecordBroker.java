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

import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.ProcessType;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.model.registry.process.TaskModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.process.ITaskStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("readAvailableProcessesForRecordBroker")
public class ReadAvailableProcessesForRecordBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Process>> {

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IProcessStore processStore;
    @Autowired
    private ITaskStore taskStore;

    @Override
    protected ServiceResponse<Process> perform(ServiceRequest<NamedValues> request) throws Exception {
        Long recordId = (Long) request.getData().get("recordId");
        String entityLookup = (String) request.getData().get("entityLookup");

        ServiceResponse<Process> response;

        EntityModel entityModel = entityStore.findByLookup(entityLookup);
        if (entityModel != null) {
            List<Process> processes = new ArrayList<Process>();
            List<ProcessModel> processModels = processStore.findAllByMainId(entityModel.getId());
            for (ProcessModel processModel : processModels) {
                TaskModel taskModel = taskStore.readWorkflowTask(processModel, entityModel, recordId);
                if (taskModel == null && ProcessType.RELATIONAL.equals(processModel.getProcessType())) {
                    Process process = factory.convertTo(Process.class, processModel);
                    processes.add(process);
                }
            }
            response = new ServiceResponse<Process>(processes, "Read available processes for Entity: '" + entityModel.getLookup() + "' with ID: " + recordId, true);
        } else {
            response = new ServiceResponse<Process>("Couldn't find Entity by lookup: " + entityLookup, false);
        }
        return response;
    }

}
