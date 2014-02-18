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

package net.firejack.platform.service.directory.broker.systemuser;

import net.firejack.platform.api.directory.domain.SystemUser;
import net.firejack.platform.core.model.user.SystemUserModel;
import net.firejack.platform.core.store.user.ISystemUserStore;
import net.firejack.platform.service.directory.broker.ReadBaseUserBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("readSystemUserBroker")
public class ReadSystemUserBroker extends ReadBaseUserBroker<SystemUserModel, SystemUser> {

    @Autowired
    @Qualifier("systemUserStore")
    protected ISystemUserStore store;

	@Override
	protected ISystemUserStore getStore() {
		return store;
	}

}
