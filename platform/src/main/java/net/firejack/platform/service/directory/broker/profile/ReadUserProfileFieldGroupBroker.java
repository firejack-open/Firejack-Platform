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

import net.firejack.platform.api.directory.domain.UserProfileFieldGroup;
import net.firejack.platform.core.broker.ReadBroker;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.user.IUserProfileFieldGroupStore;
import net.firejack.platform.core.validation.ValidationMessage;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;

@TrackDetails
@Component("readUserProfileFieldGroupBroker")
public class ReadUserProfileFieldGroupBroker extends ReadBroker<UserProfileFieldGroupModel, UserProfileFieldGroup> {

	@Autowired
    @Qualifier("userProfileFieldGroupStore")
	private IUserProfileFieldGroupStore store;

	@Override
	protected IStore<UserProfileFieldGroupModel, Long> getStore() {
		return store;
	}

	@Override
	protected UserProfileFieldGroupModel getEntity(Long id) {
		UserProfileFieldGroupModel userProfileFieldGroup = store.findUserProfileFieldGroupById(id, false);
		if (userProfileFieldGroup == null) {
			RuleValidationException validationException = new RuleValidationException();
			validationException.getValidationMessages().add(new ValidationMessage("id", "user_profile_field_group.not.exist"));
			throw new BusinessFunctionException("user_profile_field_group.not.found", new Object[]{id}, Response.Status.PRECONDITION_FAILED);
		}
		return userProfileFieldGroup;
	}
}
