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

package net.firejack.platform.service.site.broker.navigation;

import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.List;


@TrackDetails
@Component("getCachedNavigationListBroker")
public class GetCachedNavigationListBroker extends ServiceBroker
        <ServiceRequest<NamedValues<String>>, ServiceResponse<NavigationElement>> {

    public static final String PARAM_PACKAGE_LOOKUP = "package_lookup";

    @Override
    protected ServiceResponse<NavigationElement> perform(ServiceRequest<NamedValues<String>> request)
		    throws Exception {
        String packageLookup = request.getData().get(PARAM_PACKAGE_LOOKUP);
        ServiceResponse<NavigationElement> response;
        if (StringUtils.isBlank(packageLookup)) {
            response = new ServiceResponse<NavigationElement>("packageLookup parameter should not be blank.", false);
        } else {
            List<NavigationElement> navList = CacheManager.getInstance().getNavigationList(packageLookup);
            response = new ServiceResponse<NavigationElement>(navList);
        }
        return response;
    }

}