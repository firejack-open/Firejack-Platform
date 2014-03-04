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

package net.firejack.platform.service.directory.broker;

import net.firejack.platform.api.directory.domain.BaseUser;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import net.firejack.platform.core.model.user.BaseUserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.user.IBaseUserStore;
import net.firejack.platform.core.validation.ValidationMessage;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public abstract class UpdateBaseUserBroker<M extends BaseUserModel, DTO extends BaseUser> extends SaveBroker<M, DTO, DTO> {

	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    protected abstract IBaseUserStore<M> getStore();

	@Override
	protected List<ValidationMessage> specificArgumentsValidation(ServiceRequest<DTO> request) throws RuleValidationException {
		List<ValidationMessage> validationMessages = super.specificArgumentsValidation(request);
		DTO user = request.getData();

		Long registryNodeId = user.getRegistryNodeId();
		RegistryNodeModel registryNode = registryNodeStore.findById(registryNodeId);
		if (!(registryNode instanceof DirectoryModel || registryNode instanceof GroupModel)) {
			ValidationMessage validationMessage = new ValidationMessage(null, "user.resource.not.correct.registry.node.type");
			validationMessages.add(validationMessage);
		}

		return validationMessages;
	}

	@Override
	protected void save(M model) throws Exception {
		getStore().save(model);
	}
}
