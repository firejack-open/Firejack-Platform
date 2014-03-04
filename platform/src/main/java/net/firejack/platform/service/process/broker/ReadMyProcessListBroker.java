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
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IProcessStore;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class encapsulates the functionality of retrieving the list of processes for the currently logged in user
 */
@SuppressWarnings("unused")
@TrackDetails
@Component("readMyProcessListBroker")
public class ReadMyProcessListBroker extends ListBroker<ProcessModel, Process, NamedValues<String>> {

    @Autowired
    @Qualifier("processStore")
    private IProcessStore store;

    /**
     * Invokes data access layer in order to retrieve the processes by user ID and lookup prefix
     * @param parameterizedServiceRequest service request containing tlookup prefix
     * @return list of found processes
     * @throws BusinessFunctionException
     */
    @Override
    protected List<ProcessModel> getModelList(ServiceRequest<NamedValues<String>> parameterizedServiceRequest) throws BusinessFunctionException {
        IUserInfoProvider currentUser = OPFContext.getContext().getPrincipal().getUserInfoProvider();
        return store.findAllByUserId(currentUser.getId(), parameterizedServiceRequest.getData().get("lookupPrefix"), getFilter());
    }
    
}
