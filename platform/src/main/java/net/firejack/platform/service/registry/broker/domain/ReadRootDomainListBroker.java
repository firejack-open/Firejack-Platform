package net.firejack.platform.service.registry.broker.domain;

import net.firejack.platform.api.registry.domain.RootDomain;
import net.firejack.platform.core.broker.ReadAllBroker;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.store.lookup.ILookupStore;
import net.firejack.platform.core.store.registry.IRootDomainStore;
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
@Component("readRootDomainListBroker")
public class ReadRootDomainListBroker extends ReadAllBroker<RootDomainModel, RootDomain> {
	@Autowired
	@Qualifier("rootDomainStore")
	private IRootDomainStore store;

	@Override
	protected ILookupStore<RootDomainModel, Long> getStore() {
		return store;
	}
}
