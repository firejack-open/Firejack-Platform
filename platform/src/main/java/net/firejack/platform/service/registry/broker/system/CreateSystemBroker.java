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

package net.firejack.platform.service.registry.broker.system;

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.domain.System;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.store.registry.ISystemStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createSystemBroker")
public class CreateSystemBroker extends SaveBroker<SystemModel, System, RegistryNodeTree> {

	@Autowired
	private ISystemStore systemStore;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "System has been created successfully.";
	}

	@Override
	protected SystemModel convertToEntity(System model) {
		return factory.convertFrom(SystemModel.class, model);
	}

	@Override
	protected RegistryNodeTree convertToModel(SystemModel systemModel) {
		return treeNodeFactory.convertTo(systemModel);
	}

	@Override
	protected void save(SystemModel systemModel) throws Exception {
		systemStore.save(systemModel);
	}
}
