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

import net.firejack.platform.api.process.domain.Actor;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ActorModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IActorStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of searching for the actors
 */
@TrackDetails
@Component("searchActorListBroker")
public class SearchActorListBroker extends ListBroker<ActorModel, Actor, NamedValues> {

    public static final String PARAM_TERM = "term";
    public static final String PARAM_PROCESS_ID = "processId";
    public static final String PARAM_BASE_LOOKUP = "baseLookup";

    @Autowired
    private IActorStore store;

    /**
     * Invokes data access layer in order to search the actors by process ID and search term
     * @param namedValuesServiceRequest service request containing search term and IF of the process
     * @return list of found actors
     * @throws BusinessFunctionException
     */
    @Override
    protected List<ActorModel> getModelList(ServiceRequest<NamedValues> namedValuesServiceRequest) throws BusinessFunctionException {
        String term = (String) namedValuesServiceRequest.getData().get(PARAM_TERM);
        Long processId = (Long) namedValuesServiceRequest.getData().get(PARAM_PROCESS_ID);
        String baseLookup = (String) namedValuesServiceRequest.getData().get(PARAM_BASE_LOOKUP);
        return store.findAllBySearchTerm(term, processId, baseLookup, "name", "ASC");
    }

}