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
