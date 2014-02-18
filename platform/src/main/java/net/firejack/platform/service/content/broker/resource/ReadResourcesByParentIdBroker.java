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

package net.firejack.platform.service.content.broker.resource;

import net.firejack.platform.api.content.domain.Resource;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.utils.ListUtils;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("readResourcesByParentIdBroker") //TODO need to change
@TrackDetails
public class ReadResourcesByParentIdBroker
//        extends FilteredListBroker<ResourceModel, Resource, NamedValues> {
        extends ListBroker<ResourceModel, Resource, NamedValues> {

    private RegistryNodeModel registryNode;

    @Autowired
    @Qualifier("resourceStore")
    private IResourceStore<ResourceModel> resourceStore;

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    SpecifiedIdsFilter<Long> filter;

    @Override
    protected Integer getTotal(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
        Long registryNodeId = (Long) request.getData().get("registryNodeId");

        filter = getFilter();
        List<Long> exceptIds = (List<Long>) request.getData().get("exceptIds");
        List<Long> exceptResourceIds = ListUtils.removeNullableItems(exceptIds);
        filter.getUnnecessaryIds().addAll(exceptResourceIds);

        Integer total = 0;
        registryNode = registryNodeStore.findById(registryNodeId);
        if (registryNode != null) {
            total = resourceStore.findCountByLikeLookupWithFilter(registryNode.getLookup(), filter);
        }
        return total;
    }

    protected List<ResourceModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
        Paging paging = (Paging) request.getData().get("paging");
        return resourceStore.findAllByLikeLookupWithFilter(registryNode.getLookup(), filter, paging);
    }

}