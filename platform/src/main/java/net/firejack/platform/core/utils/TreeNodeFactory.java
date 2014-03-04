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

package net.firejack.platform.core.utils;

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.model.EntityType;
import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.model.registry.system.ServerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TreeNodeFactory {

    private static final String ICON_CLASS_PREFIX = "tricon-";
    private static final String ABSTRACT_ENTITY_SUFFIX = "abstract-entity";
    private static final String TYPE_ENTITY_SUFFIX = "type-entity";

    @Autowired
    private Factory factory;

    public RegistryNodeTree convertTo(RegistryNodeModel model) {
        RegistryNodeTree registryNodeTree = factory.convertTo(RegistryNodeTree.class, model);
        populateTreeNodeProperties(registryNodeTree, model);
        if (model.getChildren() != null) {
            List<RegistryNodeTree> children = convertTo(model.getChildren());
            registryNodeTree.setChildren(children);
        }
        return registryNodeTree;
    }

    public List<RegistryNodeTree> convertTo(List<RegistryNodeModel> modelList) {
        List<RegistryNodeTree> nodeList = new ArrayList<RegistryNodeTree>();
        for (RegistryNodeModel model : modelList) {
            RegistryNodeTree node = convertTo(model);
            nodeList.add(node);
        }
        return nodeList;
    }

    public void populateTreeNodeProperties(RegistryNodeTree registryNodeTree, RegistryNodeModel model) {
        boolean allowDrag = model instanceof IAllowDrag;
        registryNodeTree.setAllowDrag(allowDrag);
        boolean allowDrop = model instanceof IAllowDrop;
        registryNodeTree.setAllowDrop(allowDrop);
        registryNodeTree.setExpandable(true);
        registryNodeTree.setExpanded(false);
        registryNodeTree.setCanDelete(true);
        registryNodeTree.setCanUpdate(true);

        String iconCls;
        if (model instanceof EntityModel) {
            EntityModel entity = (EntityModel) model;
            if (entity.getTypeEntity() != null && entity.getTypeEntity()) {
                iconCls = ICON_CLASS_PREFIX + TYPE_ENTITY_SUFFIX;
                registryNodeTree.setEntitySubType(EntityType.DATA.getEntityType());
            } else if (entity.getAbstractEntity() != null && entity.getAbstractEntity()) {
                iconCls = ICON_CLASS_PREFIX + ABSTRACT_ENTITY_SUFFIX;
                registryNodeTree.setEntitySubType(EntityType.CLASSIFIER.getEntityType());
            } else {
                iconCls = ICON_CLASS_PREFIX + getStyleSuffix(model.getType());
                registryNodeTree.setEntitySubType(EntityType.STANDARD.getEntityType());
            }
        } else {
            if (model.getType() == RegistryNodeType.SUB_DOMAIN) {
                registryNodeTree.setCanUpdate(false);
            }
            iconCls = ICON_CLASS_PREFIX + getStyleSuffix(model.getType());
        }
        registryNodeTree.setIconCls(iconCls);

        if (model instanceof PackageModel) {
            if (((PackageModel) model).getSystem() != null) {
//                registryNodeTree.setAllowDrag(false);
                registryNodeTree.getParameters().put("associated", true);
            }
        } else if (model instanceof RootDomainModel) {
            registryNodeTree.setAllowDrag(false);
        }

        if (model.getMain() != null) {
            registryNodeTree.setAllowDrag(false);
            if (model instanceof ServerModel || model instanceof DatabaseModel || model instanceof FileStoreModel) {
                registryNodeTree.setCanDelete(false);
//                registryNodeTree.setCanUpdate(false);
            }
        }

        boolean isBranch = model.getChildCount() > 0 || model instanceof IFolder;
        registryNodeTree.setLeaf(!isBranch);
    }

    private String getStyleSuffix(RegistryNodeType type) {
        return type.name().toLowerCase();
    }
}
