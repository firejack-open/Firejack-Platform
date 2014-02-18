package net.firejack.platform.service.registry.broker.domain;

import net.firejack.platform.api.registry.domain.Domain;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.TreeNodeFactory;
import net.firejack.platform.core.validation.annotation.Revalidation;
import net.firejack.platform.service.aop.annotation.Changes;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Revalidation
@Component("updateDomainBroker")
@Changes("'Domain '+ name +' has been updated'")
public class UpdateDomainBroker extends OPFSaveBroker<DomainModel, Domain, RegistryNodeTree> {

	@Autowired
	@Qualifier("domainStore")
	private IRegistryNodeStore<DomainModel> store;
    @Autowired
    private TreeNodeFactory treeNodeFactory;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "Domain has updated successfully";
	}

	@Override
	protected DomainModel convertToEntity(Domain model) {
		return factory.convertFrom(DomainModel.class, model);
	}

	@Override
	protected RegistryNodeTree convertToModel(DomainModel entity) {
		return treeNodeFactory.convertTo(entity);
	}

	@Override
	protected void save(DomainModel model) throws Exception {
		store.save(model);
	}
}
