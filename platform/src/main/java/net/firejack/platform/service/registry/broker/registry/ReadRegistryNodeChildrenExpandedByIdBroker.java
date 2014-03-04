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

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.model.PageType;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TrackDetails
@Component("readRegistryNodeChildrenExpandedByIdBroker")
public class ReadRegistryNodeChildrenExpandedByIdBroker
		extends AbstractReadRegistryNodeChildrenListBroker {

	@Override
	protected List<RegistryNodeModel> getModelList(ServiceRequest<NamedValues> request) throws BusinessFunctionException {
		Long registryNodeId = (Long) request.getData().get("registryNodeId");
		PageType pageType = (PageType) request.getData().get("pageType");

        return getModelListById(registryNodeId, pageType);
	}

    protected List<RegistryNodeModel> getModelListById(Long registryNodeId, PageType pageType) {
        List<RegistryNodeModel> registryNodesWithChildren = null;
        if (pageType != null) {
            List<RegistryNodeType> filteredTypes = registryNodeTypesPageFilter.get(pageType);
            Class[] registryNodeClasses = new Class[filteredTypes.size()];
            for (int i = 0; i < filteredTypes.size(); i++) {
                registryNodeClasses[i] = filteredTypes.get(i).getClazz();
            }

            List<Long> registryNodeIds = new ArrayList<Long>();
            List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
            registryNodeStore.findCollectionParentIds(registryNodeIds, registryNodeId, collectionArrayIds);
            List<RegistryNodeModel> parentRegistryNodes = registryNodeStore.findByIdsWithFilter(registryNodeIds, null);
            Collections.reverse(parentRegistryNodes);

            for (RegistryNodeModel parentRegistryNode : parentRegistryNodes) {
                Long grandparentRegistryNodeId = null;
                RegistryNodeModel grandparentRegistryNode = parentRegistryNode.getParent();
                if (grandparentRegistryNode != null) {
                    grandparentRegistryNodeId = grandparentRegistryNode.getId();
                }

                List<RegistryNodeModel> filteredRegistryNodeChildren = new ArrayList<RegistryNodeModel>();
                List<RegistryNodeModel> registryNodes = registryNodeStore.findChildrenByParentIdAndTypes(
                    grandparentRegistryNodeId, getFilter(), registryNodeClasses);
                for (RegistryNodeModel registryNode : registryNodes) {
                    if (registryNode.isDisplayedInTree()) {
                        filteredRegistryNodeChildren.add(registryNode);
                    }
                }
                Collections.sort(filteredRegistryNodeChildren, RegistryNodeModel.REGISTRY_NODE_TYPE_COMPARATOR);

                for (RegistryNodeModel registryNode : filteredRegistryNodeChildren) {
                    if (registryNode.getId().equals(parentRegistryNode.getId()) && registryNodesWithChildren != null) {
                        registryNode.setChildren(registryNodesWithChildren);
                        break;
                    }
                }
                registryNodesWithChildren = new ArrayList<RegistryNodeModel>();
                for (RegistryNodeModel registryNode : filteredRegistryNodeChildren) {
                    registryNodesWithChildren.add(registryNode);
                }
            }
        }
        return registryNodesWithChildren;
    }

    @Override
	protected List<RegistryNodeTree> result(List<RegistryNodeTree> registryNodeTrees, List<RegistryNodeModel> registryNodeModels) {
		if (registryNodeTrees != null) {
			for (int i = 0, registryNodeTreesSize = registryNodeTrees.size(); i < registryNodeTreesSize; i++) {
				RegistryNodeTree registryNodeTree = registryNodeTrees.get(i);
				RegistryNodeModel registryNodeModel = registryNodeModels.get(i);
				//registryNodeTree.setExpanded(true);
				if (registryNodeTree.getChildren() != null) {
					result(registryNodeTree.getChildren(), registryNodeModel.getChildren());
				}
			}
		}
		registryNodeTrees = super.result(registryNodeTrees, registryNodeModels);
		if (registryNodeTrees != null) {
			for (RegistryNodeTree node : registryNodeTrees) {
                if (node.getChildren() != null) {
                    //node.setExpanded(true);
                }
			}
		}
		return registryNodeTrees;
	}
}
