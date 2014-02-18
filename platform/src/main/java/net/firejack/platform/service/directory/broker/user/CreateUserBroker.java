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

package net.firejack.platform.service.directory.broker.user;

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.service.directory.broker.CreateBaseUserBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("createUserBroker")
public class CreateUserBroker extends CreateBaseUserBroker<UserModel, User> {

	@Autowired
    @Qualifier("userStore")
	private IUserStore store;

    @Override
    protected IUserStore getStore() {
        return store;
    }

    @Override
	protected String getSuccessMessage(boolean isNew) {
		return "User has created successfully";
	}

	@Override
	protected UserModel convertToEntity(User model) {
		return factory.convertFrom(UserModel.class, model);
	}

	@Override
	protected User convertToModel(UserModel entity) {
		return factory.convertTo(User.class, entity);
	}

    @Override
	protected void save(UserModel model) throws Exception {
		String hashPassword = SecurityHelper.hash(model.getPassword());
		model.setPassword(hashPassword);
		getStore().save(model);
	}

}
