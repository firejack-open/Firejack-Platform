/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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
