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
