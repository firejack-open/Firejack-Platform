/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.service.authority.broker.permission;

import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IPermissionStore;
import net.firejack.platform.core.utils.ListUtils;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@TrackDetails
@Component("readPermissionListBrokerEx")
public class ReadPermissionListBroker extends ListBroker<PermissionModel, Permission, NamedValues> {

    public static final String PARAM_REGISTRY_NODE_ID = "registryNodeId";
    public static final String PARAM_ID_TO_EXCLUDE = "exceptIds";
    public static final String PARAM_LIMIT = "PARAM_LIMIT";
    public static final String PARAM_START = "PARAM_START";

    @Autowired
    @Qualifier("permissionStore")
    private IPermissionStore permissionStore;

    private ThreadLocal<SpecifiedIdsFilter<Long>> cachedData = new ThreadLocal<SpecifiedIdsFilter<Long>>();

    @Override
    protected List<PermissionModel> getModelList(ServiceRequest<NamedValues> request)
            throws BusinessFunctionException {
        Long registryNodeId = (Long) request.getData().get(PARAM_REGISTRY_NODE_ID);
        Integer start = (Integer) request.getData().get(PARAM_START);
        Integer limit = (Integer) request.getData().get(PARAM_LIMIT);
        SpecifiedIdsFilter<Long> filter = cachedData.get();
        cachedData.remove();
        return permissionStore.findAllByParentIdWithFilter(registryNodeId, filter, new Paging(start, limit));
    }

    @Override
    protected Integer getTotal(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
        Long registryNodeId = (Long) request.getData().get(PARAM_REGISTRY_NODE_ID);
        List<Criterion> criterions = new ArrayList<Criterion>();
        if (registryNodeId != null) {
            criterions.add(Restrictions.eq("parent.id", registryNodeId));
        }

        @SuppressWarnings("unchecked")
        SpecifiedIdsFilter<Long> filter = getFilter();
        @SuppressWarnings("unchecked")
        List<Long> exceptIds = (List<Long>) request.getData().get(PARAM_ID_TO_EXCLUDE);
        List<Long> exceptPermissionIds = ListUtils.removeNullableItems(exceptIds);
        if (exceptPermissionIds != null) {
            filter.getUnnecessaryIds().addAll(exceptPermissionIds);
        }
        cachedData.set(filter);
        try {
            return permissionStore.count(criterions, filter);
        } catch (RuntimeException e) {
            cachedData.remove();
            throw e;
        }
    }
}