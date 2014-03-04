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
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.store.registry.IFieldStore;
import net.firejack.platform.core.store.registry.IRelationshipStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@TrackDetails
@Component("readRegistryNodeFieldsByEntityBroker")
public class ReadRegistryNodeFieldsByEntityBroker extends ListBroker<RegistryNodeModel, RegistryNodeTree, SimpleIdentifier<Long>> {

    @Autowired
    private IEntityStore entityStore;
    @Autowired
    private IRelationshipStore relationshipStore;
    @Autowired
    private IFieldStore fieldStore;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

    protected List<RegistryNodeTree> convertToDTOs(List<RegistryNodeModel> registryNodes) {
        return convertTo(registryNodes);
    }

    @Override
    protected List<RegistryNodeModel> getModelList(ServiceRequest<SimpleIdentifier<Long>> request) throws BusinessFunctionException {
        Long entityId = request.getData().getIdentifier();
        if (entityId == null) {
            throw new BusinessFunctionException("Entity ID can't be empty.");
        }

        EntityModel entityModel = entityStore.findWithInheritedFieldsById(entityId);
        if (entityModel == null) {
            throw new BusinessFunctionException("Could not find Entity by ID: " + entityId);
        }

        Set<Long> unique = new HashSet<Long>();
        buildTree(entityModel, unique);

        ArrayList<RegistryNodeModel> registryNodeModels = new ArrayList<RegistryNodeModel>();
        registryNodeModels.add(entityModel);
        return registryNodeModels;
    }

    private void buildTree(EntityModel relatedEntityModel, Set<Long> unique) {
        Long entityId = relatedEntityModel.getId();

        List<FieldModel> fieldModels = fieldStore.findFieldsByRegistryNodeId(relatedEntityModel.getId());
        relatedEntityModel.setFields(fieldModels);

        List<RelationshipModel> relationshipModels = relationshipStore.findRelatedEntitiesBySourceEntityId(entityId, false, true, getFilter());
        for (RelationshipModel relationshipModel : relationshipModels) {
            if (!unique.contains(relationshipModel.getId()) && entityId.equals(relationshipModel.getSourceEntity().getId())) {
                unique.add(relationshipModel.getId());
                relationshipModel.setSourceEntity(relatedEntityModel); // it is necessary for getting correct entity with relationship in main field for getting list of ids below
                EntityModel targetEntity = relationshipModel.getTargetEntity();
                List<RegistryNodeModel> children = relatedEntityModel.getChildren();
                if (children == null) {
                    children = new ArrayList<RegistryNodeModel>();
                    relatedEntityModel.setChildren(children);
                }
                targetEntity.setMain(relationshipModel);
                children.add(targetEntity);
                if (relationshipModel.getRelationshipType() != RelationshipType.TREE) {
                    buildTree(targetEntity, unique);
                }
            }
        }
    }

    public List<RegistryNodeTree> convertTo(List<RegistryNodeModel> modelList) {
        List<RegistryNodeTree> nodeList = new ArrayList<RegistryNodeTree>();
        for (RegistryNodeModel model : modelList) {
            RegistryNodeTree node = convertTo(model);
            nodeList.add(node);
        }
        return nodeList;
    }

    public RegistryNodeTree convertTo(RegistryNodeModel model) {
        RegistryNodeTree registryNodeTree = factory.convertTo(RegistryNodeTree.class, model);
        treeNodeFactory.populateTreeNodeProperties(registryNodeTree, model);
        registryNodeTree.setAllowDrag(false);
        registryNodeTree.setAllowDrop(false);
        registryNodeTree.setLeaf(false);
        if (model.getChildren() != null) {
            List<RegistryNodeTree> children = convertTo(model.getChildren());
            registryNodeTree.setChildren(children);
        }
        if (model instanceof EntityModel) {
            EntityModel entityModel = (EntityModel) model;
            Map<String, Serializable> parameters = new HashMap<String, Serializable>();
            ArrayList<Long> relationshipIds = new ArrayList<Long>();
            findRelationshipIds(relationshipIds, entityModel);
            parameters.put("relationshipIds", relationshipIds);
            registryNodeTree.setParameters(parameters);

            List<RegistryNodeTree> children = registryNodeTree.getChildren();
            if (children == null) {
                children = new ArrayList<RegistryNodeTree>();
                registryNodeTree.setChildren(children);
            }

            List<FieldModel> fieldModels = entityModel.getFields();
            for (FieldModel fieldModel : fieldModels) {
                RegistryNodeTree fieldNodeTree = factory.convertTo(RegistryNodeTree.class, fieldModel);
                parameters = new HashMap<String, Serializable>();
                parameters.put("autoGenerated", fieldModel.getAutoGenerated());
                parameters.put("required", fieldModel.getRequired());
                parameters.put("fieldType", fieldModel.getFieldType());
                parameters.put("displayName", fieldModel.getDisplayName());
                RegistryNodeModel relationship = entityModel.getMain();
                if (relationship != null) {
                    relationshipIds = new ArrayList<Long>();
                    findRelationshipIds(relationshipIds, entityModel);
                    parameters.put("relationshipIds", relationshipIds);
                }
                fieldNodeTree.setParameters(parameters);
                fieldNodeTree.setAllowDrag(true);
                fieldNodeTree.setAllowDrop(false);
                fieldNodeTree.setExpandable(false);
                fieldNodeTree.setExpanded(false);
                fieldNodeTree.setLeaf(true);
                fieldNodeTree.setCanDelete(true);
                fieldNodeTree.setCanUpdate(true);
                children.add(fieldNodeTree);
            }
        }
        return registryNodeTree;
    }

    private void findRelationshipIds(List<Long> relationshipIds, EntityModel entityModel) {
        RelationshipModel relationship = (RelationshipModel) entityModel.getMain();
        if (relationship != null) {
            EntityModel sourceEntity = relationship.getSourceEntity();
            findRelationshipIds(relationshipIds, sourceEntity);
            relationshipIds.add(relationship.getId());
        }
    }

}