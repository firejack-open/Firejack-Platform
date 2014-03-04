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

import net.firejack.platform.api.process.domain.Case;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.CaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.process.ICaseStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class encapsulates the functionality of finding the active cases
 */
@TrackDetails
@Component("findActiveCasesBroker")
public class FindActiveCasesBroker extends ListBroker<CaseModel, Case, NamedValues> {

    @Autowired
    @Qualifier("caseStore")
    private ICaseStore caseStore;

    @Autowired
    private TaskCaseProcessor taskCaseProcessor;

    /**
     * Invokes data access layer in order to retrieve the active cases
     * @param request service request containing ID and lookup of the entity related to the cases
     * @return list of active cases
     * @throws BusinessFunctionException
     */
    @Override
    protected List<CaseModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
        String entityTypeLookup = (String) request.getData().get("entityTypeLookup");
        Long entityId = (Long) request.getData().get("entityId");
        return caseStore.findActive(entityTypeLookup, entityId, null);
    }

    @Override
    protected List<Case> convertToDTOs(List<CaseModel> entities) {
        List<Case> processCases = super.convertToDTOs(entities);
        Map<String, List<Process>> processesMap = new HashMap<String, List<Process>>();
        for (Case processCase : processCases) {
            taskCaseProcessor.registerCaseProcess(processCase, processesMap);
        }
        taskCaseProcessor.initializeCaseStrategy(processesMap);
        return processCases;
    }
}
