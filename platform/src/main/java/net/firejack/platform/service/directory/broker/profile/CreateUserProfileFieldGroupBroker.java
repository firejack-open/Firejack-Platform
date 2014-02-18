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
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.user.IUserProfileFieldGroupStore;
import net.firejack.platform.core.validation.ValidationMessage;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("createUserProfileFieldGroupBroker")
public class CreateUserProfileFieldGroupBroker extends OPFSaveBroker<UserProfileFieldGroupModel, UserProfileFieldGroup, UserProfileFieldGroup> {

	@Autowired
    @Qualifier("userProfileFieldGroupStore")
	private IUserProfileFieldGroupStore store;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "User profile field group has created successfully";
	}

	@Override
	protected List<ValidationMessage> specificArgumentsValidation(ServiceRequest<UserProfileFieldGroup> request) throws RuleValidationException {
		UserProfileFieldGroup userProfileFieldGroupVO = request.getData();
		List<ValidationMessage> validationMessages = super.specificArgumentsValidation(request);
		UserProfileFieldGroupModel sameUserProfileFieldGroup = store.findUserProfileFieldGroupByRegistryNodeIdAndName(userProfileFieldGroupVO.getParentId(), userProfileFieldGroupVO.getName());
		if (sameUserProfileFieldGroup != null) {
			validationMessages.add(new ValidationMessage(null, "user_profile_field_group.already.exist.with.name"));
		}
		return validationMessages;
	}

	@Override
	protected UserProfileFieldGroupModel convertToEntity(UserProfileFieldGroup model) {
		return factory.convertFrom(UserProfileFieldGroupModel.class, model);
	}

	@Override
	protected UserProfileFieldGroup convertToModel(UserProfileFieldGroupModel entity) {
		return factory.convertTo(UserProfileFieldGroup.class, entity);
	}

	@Override
	protected void save(UserProfileFieldGroupModel model) throws Exception {
		model.setChildCount(0);
		store.saveOrUpdate(model);
	}
}
