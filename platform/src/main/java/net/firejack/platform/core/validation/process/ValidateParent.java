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

package net.firejack.platform.core.validation.process;


import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.domain.TreeNode;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.validation.annotation.DomainType;
import net.firejack.platform.core.validation.annotation.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ValidateParent {

	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	public <T extends BaseEntity> void validate(T domain, Validate validate) {
		DomainType[] parents = validate.parents();
		if (parents.length == 0)
			return;

		if (!(domain instanceof TreeNode))
			throw new BusinessFunctionException("Domain not extended TreeNode");

		Long parentId = ((TreeNode) domain).getParentId();

		if (parentId == null)
			throw new BusinessFunctionException("Parent not defined");

		LookupModel model = registryNodeStore.findById(parentId);

		if (model == null)
			throw new BusinessFunctionException("Parent not found");

		Class<? extends LookupModel> cls = model.getClass();

		for (DomainType parent : parents) {
			RegistryNodeType type = RegistryNodeType.valueOf(parent.name());
			if (type.getClazz() == cls) {
			   return;
			}
		}

		throw new BusinessFunctionException("Error incorrect parent type");
	}
}
