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
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.user.SystemUserModel;
import net.firejack.platform.core.store.registry.ISystemStore;
import net.firejack.platform.core.store.user.ISystemUserStore;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.service.directory.broker.CreateBaseUserBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createSystemUserBroker")
public class CreateSystemUserBroker extends CreateBaseUserBroker<SystemUserModel, SystemUser> {

	@Autowired
    @Qualifier("systemUserStore")
	private ISystemUserStore store;

    @Autowired
    @Qualifier("systemStore")
    private ISystemStore systemStore;

    @Override
    protected ISystemUserStore getStore() {
        return store;
    }

    @Override
	protected String getSuccessMessage(boolean isNew) {
		return "System User has created successfully";
	}

	@Override
	protected SystemUserModel convertToEntity(SystemUser model) {
		return factory.convertFrom(SystemUserModel.class, model);
	}

	@Override
	protected SystemUser convertToModel(SystemUserModel entity) {
		return factory.convertTo(SystemUser.class, entity);
	}

    @Override
	protected void save(SystemUserModel model) throws Exception {
        if (model.getSystem() != null) {
            SystemModel system = systemStore.findById(model.getSystem().getId());
            model.setConsumerKey(system.getServerName());
        }
        model.setConsumerSecret(SecurityHelper.generateRandomSequence(32));
		getStore().save(model);
	}

}
