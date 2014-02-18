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
import org.springframework.stereotype.Component;

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

@TrackDetails
@Component("updateActionBroker")
public class UpdateActionBroker extends OPFSaveBroker<ActionModel, Action, RegistryNodeTree> {

	@Autowired
	private IActionStore store;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Action has been updated successfully.";
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
				throw new BusinessFunctionException(null, "action.should.exist.input.vo", model.getMethod());
			}
		}
		if (model.getOutputVOEntity() == null) {
			throw new BusinessFunctionException(null, "action.should.exist.output.vo", model.getMethod());
		}

		if (HTTPMethod.GET.equals(model.getMethod()) || HTTPMethod.DELETE.equals(model.getMethod())) {
			model.setInputVOEntity(null);
		}

		store.save(model);
	}
}

