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

import net.firejack.platform.api.process.domain.ActivityType;
import net.firejack.platform.api.process.domain.Process;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of searching for the processes
 */
@TrackDetails
@Component("searchProcessListBroker")
public class SearchProcessListBroker extends ListBroker<ProcessModel, Process, NamedValues> {

    public static final String PARAM_TERM = "term";
    public static final String PARAM_HUMAN_PROCESS = "humanProcess";

    @Autowired
    private IProcessStore store;

    /**
     * Invokes data access layer in order to find processes by search term
     * @param namedValuesServiceRequest service request containing the serach term
     * @return list of found processes
     * @throws BusinessFunctionException
     */
    @Override
    protected List<ProcessModel> getModelList(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        String term = (String) namedValuesServiceRequest.getData().get(PARAM_TERM);
        Boolean humanProcess = (Boolean) namedValuesServiceRequest.getData().get(PARAM_HUMAN_PROCESS);
        return humanProcess != null && humanProcess ?
                store.findAllBySearchTermAndActivityType(term, ActivityType.HUMAN, "name", "ASC", null) :
                store.findAllBySearchTerm(term, "name", "ASC");
    }
   
}
