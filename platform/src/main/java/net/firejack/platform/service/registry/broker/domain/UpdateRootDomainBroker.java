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

package net.firejack.platform.service.registry.broker.domain;

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.registry.domain.RootDomain;
import net.firejack.platform.core.broker.SaveBroker;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.store.registry.IRootDomainStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("updateRootDomainBroker")
public class UpdateRootDomainBroker extends SaveBroker<RootDomainModel, RootDomain, RegistryNodeTree> {

	@Autowired
	private IRootDomainStore store;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Root Domain has been updated successfully.";
	}

	@Override
	protected RootDomainModel convertToEntity(RootDomain rootDomain) {
		return factory.convertFrom(RootDomainModel.class, rootDomain);
	}

	@Override
	protected RegistryNodeTree convertToModel(RootDomainModel rootDomainModel) {
		return treeNodeFactory.convertTo(rootDomainModel);
	}

	@Override
	protected void save(RootDomainModel model) throws Exception {
		store.save(model);
	}
}
