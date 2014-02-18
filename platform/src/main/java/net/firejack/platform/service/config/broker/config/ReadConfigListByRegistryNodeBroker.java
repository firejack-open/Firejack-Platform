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

package net.firejack.platform.service.config.broker.config;

import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.config.ConfigModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IConfigStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("readConfigListByRegistryNodeBroker")
public class ReadConfigListByRegistryNodeBroker extends ListBroker<ConfigModel, Config, SimpleIdentifier<Long>> {

	@Autowired
	private IConfigStore store;
	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	@Override
	protected List<ConfigModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> request) throws BusinessFunctionException {
		Long registryNodeId = request.getData().getIdentifier();

		List<Long> registryNodeIds = new ArrayList<Long>();
		List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
		registryNodeStore.findCollectionChildrenIds(registryNodeIds, registryNodeId, collectionArrayIds);
		registryNodeIds.add(registryNodeId);

		return store.findAllByParentIdsWithFilter(registryNodeIds, getFilter(), null);
	}
}
