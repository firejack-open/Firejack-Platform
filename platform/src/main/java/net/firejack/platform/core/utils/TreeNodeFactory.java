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
