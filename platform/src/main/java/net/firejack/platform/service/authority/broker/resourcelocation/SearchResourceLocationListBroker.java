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

package net.firejack.platform.service.authority.broker.resourcelocation;

import net.firejack.platform.api.authority.domain.ResourceLocation;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IResourceLocationStore;
import net.firejack.platform.core.utils.ListUtils;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@TrackDetails
@Component("searchResourceLocationListBrokerEx")
public class SearchResourceLocationListBroker extends ListBroker
        <ResourceLocationModel, ResourceLocation, NamedValues<Object>> {

    public static final String PARAM_REGISTRY_NODE_ID = "registryNodeId";
    public static final String PARAM_TERM = "term";
    public static final String PARAM_ID_TO_EXCLUDE = "exceptIds";
    public static final String PARAM_START = "PARAM_START";
    public static final String PARAM_LIMIT = "PARAM_LIMIT";

    @Autowired
    @Qualifier("resourceLocationStore")
    private IResourceLocationStore store;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    private ThreadLocal<Tuple<SpecifiedIdsFilter<Long>, List<Long>>> contextData =
            new ThreadLocal<Tuple<SpecifiedIdsFilter<Long>, List<Long>>>();

    @Override
    @SuppressWarnings("unchecked")
    protected List<ResourceLocationModel> getModelList(ServiceRequest<NamedValues<Object>> request)
            throws BusinessFunctionException {
        Long registryNodeId = (Long) request.getData().get(PARAM_REGISTRY_NODE_ID);
        String term = (String) request.getData().get(PARAM_TERM);

        Integer start = (Integer) request.getData().get(PARAM_START);
        Integer limit = (Integer) request.getData().get(PARAM_LIMIT);

        List<ResourceLocationModel> resourceLocationList;
        Paging paging = new Paging(start, limit);
        Tuple<SpecifiedIdsFilter<Long>, List<Long>> tuple = contextData.get();
        contextData.remove();
        SpecifiedIdsFilter<Long> filter = tuple.getKey();
        if (registryNodeId == null) {
            resourceLocationList = store.findAllBySearchTermWithFilter(term, filter);
        } else {
            List<Long> registryNodeIds = tuple.getValue();
            resourceLocationList = store.findAllBySearchTermWithFilter(registryNodeIds, term, filter, paging);
        }
        return resourceLocationList;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Integer getTotal(ServiceRequest<NamedValues<Object>> request) throws BusinessFunctionException {
        SpecifiedIdsFilter<Long> filter = getFilter();
        List<Long> exceptIds = (List<Long>) request.getData().get(PARAM_ID_TO_EXCLUDE);
        if (exceptIds != null && !exceptIds.isEmpty()) {
            List<Long> exceptPermissionIds = ListUtils.removeNullableItems(exceptIds);
            filter.getUnnecessaryIds().addAll(exceptPermissionIds);
        }
        Long registryNodeId = (Long) request.getData().get(PARAM_REGISTRY_NODE_ID);
        String term = (String) request.getData().get(PARAM_TERM);
        List<Criterion> restrictions = new ArrayList<Criterion>();
        restrictions.add(Restrictions.like("lookup", '%' + term + '%'));
        Integer total;
        List<Long> registryNodeIds;
        if (registryNodeId == null) {
            registryNodeIds = null;
        } else {
            registryNodeIds = new ArrayList<Long>();
            List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
            registryNodeStore.findCollectionChildrenIds(registryNodeIds, registryNodeId, collectionArrayIds);
            registryNodeIds.add(registryNodeId);
            restrictions.add(Restrictions.in("parent.id", registryNodeIds));
        }
        contextData.set(new Tuple<SpecifiedIdsFilter<Long>, List<Long>>(filter, registryNodeIds));
        try {
            total = registryNodeId == null ? store.count(restrictions, filter) : store.count(restrictions, filter);
        } catch (RuntimeException e) {
            contextData.remove();
            throw e;
        }
        return total;
    }
}