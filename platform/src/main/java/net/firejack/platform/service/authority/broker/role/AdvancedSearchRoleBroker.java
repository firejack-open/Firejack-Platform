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

package net.firejack.platform.service.authority.broker.role;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.RoleStore;
import net.firejack.platform.core.utils.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;


@Component
public class AdvancedSearchRoleBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Role>> {

    public static final String PARAM_QUERY_PARAMETERS = "queryParameters";
    public static final String PARAM_OFFSET = "offset";
    public static final String PARAM_LIMIT = "limit";
    //public static final String PARAM_SORT_ORDERS = "sortOrders";

    @Autowired
    private RoleStore store;

    @Override
    protected ServiceResponse<Role> perform(ServiceRequest<NamedValues> request) throws Exception {
        AdvancedSearchParams queryParameters = (AdvancedSearchParams) request.getData().get(PARAM_QUERY_PARAMETERS);
        Integer offset = (Integer) request.getData().get(PARAM_OFFSET);
        Integer limit = (Integer) request.getData().get(PARAM_LIMIT);

        List<List<SearchQuery>> searchQueries;
        if (queryParameters == null || queryParameters.getSearchQueries() == null) {
            searchQueries = null;
        } else {
            ObjectMapper mapper = new ObjectMapper();
            searchQueries = new LinkedList<List<SearchQuery>>();
            for (AdvancedSearchQueryOperand queryOperand : queryParameters.getSearchQueries()) {
                if (queryOperand.getCriteriaList() != null && !queryOperand.getCriteriaList().isEmpty()) {
                    List<SearchQuery> innerList = new LinkedList<SearchQuery>();
                    for (String queryValue : queryOperand.getCriteriaList()) {
                        SearchQuery searchQuery = mapper.readValue(queryValue, SearchQuery.class);
                        innerList.add(searchQuery);
                    }
                    searchQueries.add(innerList);
                }
            }
        }

        Integer total = store.advancedSearchCount(searchQueries);
        List<Role> roles = null;
        if (total > 0) {
            List<SortField> sortFields = queryParameters == null ||
                    queryParameters.getSortFields() == null ? null : queryParameters.getSortFields();
            List<RoleModel> models = store.advancedSearch(searchQueries, new Paging(offset, limit, sortFields));
            roles = factory.convertTo(Role.class, models);
        }

/*
        String queryParameters = (String) request.getData().get(PARAM_QUERY_PARAMETERS);
        Integer offset = (Integer) request.getData().get(PARAM_OFFSET);
        Integer limit = (Integer) request.getData().get(PARAM_LIMIT);

        ObjectMapper mapper;
        List<List<SearchQuery>> searchQueries;
        if (queryParameters == null || queryParameters.isEmpty()) {
            searchQueries = null;
            mapper = null;
        } else {
            mapper = new ObjectMapper();
            searchQueries = mapper.readValue(
                    queryParameters, TypeFactory.parametricType(
                    List.class, TypeFactory.parametricType(List.class, SearchQuery.class)));
        }

        Integer total = store.advancedSearchCount(searchQueries);
        List<Role> roles = null;
        if (total > 0) {
            String sortOrders = (String) request.getData().get(PARAM_SORT_ORDERS);
            List<SortField> sortFields;
            if (StringUtils.isBlank(sortOrders)) {
                sortFields = null;
            } else {
                mapper = mapper == null ? new ObjectMapper() : mapper;
                sortFields = mapper.readValue(sortOrders, new TypeReference<List<SortField>>() {});
            }
            List<RoleModel> models = store.advancedSearch(searchQueries, new Paging(offset, limit, sortFields));
            roles = factory.convertTo(Role.class, models);
        }

*/
        return new ServiceResponse<Role>(roles, "Action completed successfully.", true, total);
    }

}