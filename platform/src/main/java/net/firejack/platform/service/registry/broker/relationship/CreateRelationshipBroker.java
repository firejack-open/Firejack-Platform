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

package net.firejack.platform.service.registry.broker.relationship;

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.domain.Relationship;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.store.registry.IRelationshipStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.service.aop.annotation.Changes;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createRelationshipBroker")
@Changes("'New Relationship '+ name +' has been created'")
public class CreateRelationshipBroker extends OPFSaveBroker<RelationshipModel, Relationship, RegistryNodeTree> {

	@Autowired
	private IRelationshipStore store;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Relationship has been created successfully.";
	}

	@Override
	protected RelationshipModel convertToEntity(Relationship model) {
		return factory.convertFrom(RelationshipModel.class, model);
	}

	@Override
	protected RegistryNodeTree convertToModel(RelationshipModel relationshipModel) {
		return treeNodeFactory.convertTo(relationshipModel);
	}

	@Override
	protected void save(RelationshipModel model) throws Exception {
        if (RelationshipType.TREE.equals(model.getRelationshipType())) {
            model.setTargetEntity(model.getSourceEntity());
        }
		store.save(model);
	}
}
