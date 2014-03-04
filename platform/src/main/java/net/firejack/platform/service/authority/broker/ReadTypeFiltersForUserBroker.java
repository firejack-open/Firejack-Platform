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

package net.firejack.platform.service.authority.broker;

import net.firejack.platform.api.authority.domain.TypeFilter;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.IdFilter;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@TrackDetails
@Component("readTypeFiltersForUserBroker")
public class ReadTypeFiltersForUserBroker extends ServiceBroker
        <ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<TypeFilter>> {

    @Override
    protected ServiceResponse<TypeFilter> perform(ServiceRequest<SimpleIdentifier<Long>> request)
		    throws Exception {
        Long userId = request.getData().getIdentifier();
        Map<String, IdFilter> idFiltersForUser = CacheManager.getInstance().getIdFiltersForUser(userId);
        List<TypeFilter> typeFilterList = new ArrayList<TypeFilter>();
        if (idFiltersForUser != null) {
            for (Map.Entry<String, IdFilter> idFilterEntry : idFiltersForUser.entrySet()) {
                IdFilter idFilter = idFilterEntry.getValue();

                TypeFilter typeFilter = new TypeFilter();
                typeFilter.setType(idFilterEntry.getKey());
                typeFilter.setIdFilter(idFilter);

                typeFilterList.add(typeFilter);
            }
        }
        return new ServiceResponse<TypeFilter>(typeFilterList, null, true);
    }
}