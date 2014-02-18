package net.firejack.platform.service.registry.broker.registry;

import net.firejack.platform.api.registry.model.PageType;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

@TrackDetails
@Component("readRegistryNodeChildrenListBroker")
public class ReadRegistryNodeChildrenBroker
		extends AbstractReadRegistryNodeChildrenListBroker {

    @Autowired
    private IPackageStore packageStore;

	@Override
	protected List<RegistryNodeModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
		Long registryNodeId = (Long) request.getData().get("registryNodeId");
		if (registryNodeId == 0) {
			registryNodeId = null;
            String packageLookup = (String) request.getData().get("packageLookup");
            if (StringUtils.isNotBlank(packageLookup)) {
                PackageModel packageModel = packageStore.findByLookup(packageLookup);
                if (packageModel != null) {
                    registryNodeId = packageModel.getId();
                }
            }
		}

		PageType pageType = (PageType) request.getData().get("pageType");

		List<RegistryNodeModel> filteredRegistryNodes = new ArrayList<RegistryNodeModel>();
		if (pageType != null) {
			List<RegistryNodeType> filteredTypes = registryNodeTypesPageFilter.get(pageType);
			Class[] registryNodeClasses = new Class[filteredTypes.size()];
			for (int i = 0; i < filteredTypes.size(); i++) {
				registryNodeClasses[i] = filteredTypes.get(i).getClazz();
			}
			List<RegistryNodeModel> registryNodes =
					registryNodeStore.findChildrenByParentIdAndTypes(registryNodeId, getFilter(), registryNodeClasses);
			for (RegistryNodeModel registryNode : registryNodes) {
				if (registryNode.isDisplayedInTree()) {
					filteredRegistryNodes.add(registryNode);
				}
			}
		}
		Collections.sort(filteredRegistryNodes, RegistryNodeModel.REGISTRY_NODE_TYPE_COMPARATOR);
		return filteredRegistryNodes;
	}
}
