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

package net.firejack.platform.service.directory.broker.profile;

import net.firejack.platform.core.broker.DeleteLookupModelBroker;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.model.user.UserProfileFieldModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.user.IUserProfileFieldGroupStore;
import net.firejack.platform.core.store.user.IUserProfileFieldStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("deleteUserProfileFieldGroupBroker")
public class DeleteUserProfileFieldGroupBroker extends DeleteLookupModelBroker<UserProfileFieldGroupModel> {

	@Autowired
    @Qualifier("userProfileFieldStore")
	private IUserProfileFieldStore profileFieldStore;
	@Autowired
    @Qualifier("userProfileFieldGroupStore")
	protected IUserProfileFieldGroupStore store;

	@Override
	protected String getSuccessMessage() {
		return "User profile field group has deleted successfully";
	}

	@Override
	protected IStore<UserProfileFieldGroupModel, Long> getStore() {
		return store;
	}

	@Override
	protected void delete(Long id) {
		UserProfileFieldGroupModel userProfileFieldGroup = store.findUserProfileFieldGroupById(id, true);
		if (userProfileFieldGroup != null) {
			for (UserProfileFieldModel userProfileField : userProfileFieldGroup.getUserProfileFields()) {
				userProfileField.setUserProfileFieldGroup(null);
				profileFieldStore.saveOrUpdate(userProfileField);
			}
			store.delete(userProfileFieldGroup);
		}
	}
}

