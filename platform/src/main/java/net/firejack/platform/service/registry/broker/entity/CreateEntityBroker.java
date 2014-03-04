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

package net.firejack.platform.service.registry.broker.entity;

import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.model.IndexType;
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.field.IndexModel;
import net.firejack.platform.core.store.registry.IActionStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.service.aop.annotation.Changes;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@TrackDetails
@Component("createEntityBroker")
@Changes("'New Entity '+ name +' has been created'")
public class CreateEntityBroker extends OPFSaveBroker<EntityModel, Entity, RegistryNodeTree> {

	@Autowired
	private IEntityStore store;
	@Autowired
	private IActionStore actionStore;
    @Autowired
    private TreeNodeFactory treeNodeFactory;


	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Entity has created successfully";
	}

	@Override
	protected EntityModel convertToEntity(Entity model) {
		return factory.convertFrom(EntityModel.class, model);
	}

	@Override
	protected RegistryNodeTree convertToModel(EntityModel entity) {
		RegistryNodeTree tree = treeNodeFactory.convertTo(entity);
		if (entity.getTypeEntity()) {
			tree.setExpanded(true);
			tree.setLeaf(true);
			tree.setChildren(null);
		}
		return tree;
	}

	@Override
	protected void save(EntityModel model) throws Exception {
        FieldModel idField = null;
        for (FieldModel fieldModel : model.getFields()) {
            if ("id".equalsIgnoreCase(fieldModel.getName())) {
                idField = fieldModel;
                break;
            }
        }
        if (idField != null) {
            IndexModel primaryKeyIndexModel = new IndexModel();
            primaryKeyIndexModel.setIndexType(IndexType.PRIMARY);
            primaryKeyIndexModel.setName(IndexType.PRIMARY.name());
            primaryKeyIndexModel.setParent(model);
            primaryKeyIndexModel.setFields(Arrays.asList(idField));
            model.setIndexes(Arrays.asList(primaryKeyIndexModel));
        }

		store.save(model);
		List<RegistryNodeModel> children = model.getChildren();
		if (children == null) {
			children = new ArrayList<RegistryNodeModel>();
			model.setChildren(children);
		}
		if (!model.getTypeEntity()) {
			List<EntityModel> typeEntities = store.findEntriesByParentId(model, null, null);
			children.addAll(typeEntities);

			List<ActionModel> actions = actionStore.findEntriesByParentId(model, null, null);
			children.addAll(actions);
		}
	}
}
