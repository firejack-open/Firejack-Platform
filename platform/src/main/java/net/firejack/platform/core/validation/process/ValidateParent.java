package net.firejack.platform.core.validation.process;
/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */


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
