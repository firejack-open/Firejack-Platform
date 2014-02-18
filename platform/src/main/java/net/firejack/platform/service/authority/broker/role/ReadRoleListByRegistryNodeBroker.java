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

package net.firejack.platform.service.authority.broker.role;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.IRoleStore;
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
@Component("readRoleListByRegistryNodeBrokerEx")
public class ReadRoleListByRegistryNodeBroker extends ListBroker<RoleModel, Role, NamedValues<Object>> {

    public static final String PARAM_REGISTRY_NODE_ID = "PARAM_REGISTRY_NODE_ID";
    public static final String PARAM_START = "PARAM_START";
    public static final String PARAM_LIMIT = "PARAM_LIMIT";

    @Autowired
    @Qualifier("roleStore")
    private IRoleStore store;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    private ThreadLocal<List<Long>> contextData = new ThreadLocal<List<Long>>();

    @Override
    protected List<RoleModel> getModelList(ServiceRequest<NamedValues<Object>> request)
            throws BusinessFunctionException {
        Integer start = (Integer) request.getData().get(PARAM_START);
        Integer limit = (Integer) request.getData().get(PARAM_LIMIT);

        List<Long> registryNodeIds = contextData.get();
        contextData.remove();

        return store.findAllByParentIdsWithFilter(registryNodeIds, getFilter(), new Paging(start, limit));
    }

    @Override
    protected Integer getTotal(ServiceRequest<NamedValues<Object>> request) throws BusinessFunctionException {
        Long registryNodeId = (Long) request.getData().get(PARAM_REGISTRY_NODE_ID);
        List<Long> registryNodeIds = new ArrayList<Long>();
        List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
        registryNodeStore.findCollectionChildrenIds(registryNodeIds, registryNodeId, collectionArrayIds);
        registryNodeIds.add(registryNodeId);
        contextData.set(registryNodeIds);

        List<Criterion> criterions = new ArrayList<Criterion>();
        criterions.add(Restrictions.in("parent.id", registryNodeIds));
        try {
            return store.count(criterions, getFilter());
        } catch (RuntimeException e) {
            contextData.remove();
            throw e;
        }
    }
}