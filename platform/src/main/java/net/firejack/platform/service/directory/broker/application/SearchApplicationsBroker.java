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

package net.firejack.platform.service.directory.broker.application;

import net.firejack.platform.api.directory.domain.Application;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.directory.ApplicationModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IApplicationStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@TrackDetails
@Component
public class SearchApplicationsBroker extends ServiceBroker
        <ServiceRequest<NamedValues<String>>, ServiceResponse<Application>> {

    public static final String PARAM_PACKAGE_LOOKUP = "PARAM_PACKAGE_LOOKUP";
    public static final String PARAM_SEARCH_TERM = "PARAM_SEARCH_TERM";

    @Autowired
    @Qualifier("applicationStore")
    private IApplicationStore store;

    @Override
    protected ServiceResponse<Application> perform(ServiceRequest<NamedValues<String>> request) throws Exception {
        String packageLookup = request.getData().get(PARAM_PACKAGE_LOOKUP);
        String searchTerm = request.getData().get(PARAM_SEARCH_TERM);

        ServiceResponse<Application> response;
        if (StringUtils.isBlank(packageLookup)) {
            response = new ServiceResponse<Application>("Package lookup should not be null.", false);
        } else {
            List<ApplicationModel> modelList = store.searchApplications(packageLookup, searchTerm);
            List<Application> applications = factory.convertTo(Application.class, modelList);
            response = applications == null || applications.isEmpty() ?
                    new ServiceResponse<Application>("Applications were not found.", true) :
                    new ServiceResponse<Application>(applications, "Applications were found.", true);
        }
        return response;
    }
}