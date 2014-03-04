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

package net.firejack.platform.service.directory.broker;

import net.firejack.platform.api.directory.domain.BaseUser;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.user.BaseUserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.user.IBaseUserStore;
import net.firejack.platform.core.utils.ListUtils;
import net.firejack.platform.core.utils.SortOrder;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Order;

import java.util.List;

public abstract class ReadBaseUserListBroker<M extends BaseUserModel, DTO extends BaseUser>
        extends ListBroker<M, DTO, NamedValues<Object>> {

    public static final String PARAM_TERM = "term";
    public static final String PARAM_EXCEPT_IDS = "exceptIds";
    public static final String PARAM_OFFSET = "offset";
    public static final String PARAM_LIMIT = "limit";
    public static final String PARAM_SORT_COLUMN = "sortColumn";
    public static final String PARAM_SORT_DIRECTION = "sortDirection";

    protected abstract IBaseUserStore<M> getStore();

	@Override
    @SuppressWarnings("unchecked")
	protected List<M> getModelList(ServiceRequest<NamedValues<Object>> request) throws BusinessFunctionException {
        NamedValues<Object> data = request.getData();
        String term = (String) data.get(PARAM_TERM);
		List<Long> exceptIds = (List<Long>) data.get(PARAM_EXCEPT_IDS);
        Integer offset = (Integer) data.get(PARAM_OFFSET);
        Integer limit = (Integer) data.get(PARAM_LIMIT);
        String sortColumn = (String) data.get(PARAM_SORT_COLUMN);
        SortOrder sortOrder = (SortOrder) data.get(PARAM_SORT_DIRECTION);
        SpecifiedIdsFilter filter = getFilter(true);
		List<M> users;

		if (exceptIds != null) {
			List<Long> exceptPermissionIds = ListUtils.removeNullableItems(exceptIds);
			filter.getUnnecessaryIds().addAll(exceptPermissionIds);
			filter.setAll(true);
		}

		if (StringUtils.isNotBlank(term)) {
            if (StringUtils.isBlank(sortColumn)) {
                Order order = sortOrder == null ? Order.asc(sortColumn) :
                        sortOrder == SortOrder.ASC ? Order.asc(sortColumn) : Order.desc(sortColumn);
                users = getStore().findAllByRegistryNodeIdsAndSearchTermWithFilter(null, term, filter, limit, offset, order);
            } else {
                users = getStore().findAllByRegistryNodeIdsAndSearchTermWithFilter(null, term, filter, limit, offset);
            }
		} else {
			users = getStore().findAllWithFilter(filter);
		}
		for (M user : users) {
			user.setUserRoles(null);
		}

		return users;
	}
}
