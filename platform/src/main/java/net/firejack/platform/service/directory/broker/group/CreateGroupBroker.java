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

package net.firejack.platform.service.directory.broker.group;

import net.firejack.platform.api.directory.domain.Group;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import net.firejack.platform.core.store.registry.IGroupStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createGroupBroker")
public class CreateGroupBroker extends OPFSaveBroker<GroupModel, Group, RegistryNodeTree> {

	@Autowired
	private IGroupStore store;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Group has been created successfully.";
	}

	@Override
	protected GroupModel convertToEntity(Group model) {
		return factory.convertFrom(GroupModel.class, model);
	}

	@Override
	protected RegistryNodeTree convertToModel(GroupModel entity) {
		return factory.convertTo(RegistryNodeTree.class, entity);
	}

	@Override
	protected void save(GroupModel model) throws Exception {
		store.save(model);
	}
}
