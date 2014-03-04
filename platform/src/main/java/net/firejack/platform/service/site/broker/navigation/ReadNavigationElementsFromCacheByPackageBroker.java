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
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

@Deprecated
@Component("readNavigationElementsFromCacheByPackageBroker")
@TrackDetails
public class ReadNavigationElementsFromCacheByPackageBroker
        extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<NavigationElement>> {

    private static final String MSG_PACKAGE_LOOKUP_IS_BLANK = "Package lookup for retrieval of navigation elements should not be blank.";

    @Override
    protected ServiceResponse<NavigationElement> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        String packageLookup = request.getData().getIdentifier();
        ServiceResponse<NavigationElement> response;
//        if (StringUtils.isBlank(packageLookup)) {
            response = new ServiceResponse(MSG_PACKAGE_LOOKUP_IS_BLANK, false);
//        } else {
//            List<NavigationVO> navigationList = CacheManager.getInstance().getNavigationList(packageLookup);
//            if (navigationList != null) {
//
//            }
//            response = new ServiceResponse<NavigationElement>(navigationList);
//        }
        return response;
    }

}