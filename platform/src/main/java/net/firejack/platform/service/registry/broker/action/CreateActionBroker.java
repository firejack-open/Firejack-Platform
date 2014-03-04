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

package net.firejack.platform.service.registry.broker.action;

import net.firejack.platform.api.registry.domain.Action;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.store.registry.IActionStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createActionBroker")
public class CreateActionBroker extends OPFSaveBroker<ActionModel, Action, RegistryNodeTree> {

	@Autowired
	@Qualifier("actionStore")
	private IActionStore store;

    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Action has been created successfully.";
	}

	@Override
	protected ActionModel convertToEntity(Action model) {
		return factory.convertFrom(ActionModel.class, model);
	}

	@Override
	protected RegistryNodeTree convertToModel(ActionModel entity) {
		return treeNodeFactory.convertTo(entity);
	}

	@Override
	protected void save(ActionModel model) throws Exception {
		if (HTTPMethod.POST.equals(model.getMethod()) || HTTPMethod.PUT.equals(model.getMethod())) {
			if (model.getInputVOEntity() == null) {
				throw new BusinessFunctionException("action.should.exist.input.vo", model.getMethod());
			}
		}
		if (model.getOutputVOEntity() == null) {
			throw new BusinessFunctionException("action.should.exist.output.vo", model.getMethod());
		}

		if (HTTPMethod.GET.equals(model.getMethod()) || HTTPMethod.DELETE.equals(model.getMethod())) {
			model.setInputVOEntity(null);
		}

		store.save(model);
	}
}
