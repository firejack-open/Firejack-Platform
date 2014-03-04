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
