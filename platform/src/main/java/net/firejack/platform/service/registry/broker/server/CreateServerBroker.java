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

package net.firejack.platform.service.registry.broker.server;

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.domain.Server;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.store.registry.IServerStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.service.registry.broker.SaveAliasableBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createServerBroker")
public class CreateServerBroker extends SaveAliasableBroker<ServerModel, Server, RegistryNodeTree> {

	@Autowired
	private IServerStore store;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Server has been created successfully.";
	}

	@Override
	protected ServerModel convertToEntity(Server model) {
		return factory.convertFrom(ServerModel.class, model);
	}

	@Override
	protected RegistryNodeTree convertToModel(ServerModel serverModel) {
		return treeNodeFactory.convertTo(serverModel);
	}

	@Override
	protected void save(ServerModel model) throws Exception {
		store.save(model);
	}
}
