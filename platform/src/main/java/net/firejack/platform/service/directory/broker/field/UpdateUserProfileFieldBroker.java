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

package net.firejack.platform.service.directory.broker.field;

import net.firejack.platform.api.directory.domain.UserProfileField;
import net.firejack.platform.api.directory.domain.UserProfileFieldTree;
import net.firejack.platform.core.broker.OPFSaveBroker;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.model.user.UserProfileFieldModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.user.IUserProfileFieldGroupStore;
import net.firejack.platform.core.store.user.IUserProfileFieldStore;
import net.firejack.platform.core.validation.ValidationMessage;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("updateUserProfileFieldBroker")
public class UpdateUserProfileFieldBroker extends OPFSaveBroker<UserProfileFieldModel, UserProfileField, UserProfileFieldTree> {

	@Autowired
    @Qualifier("userProfileFieldStore")
	private IUserProfileFieldStore store;
	@Autowired
    @Qualifier("userProfileFieldGroupStore")
	protected IUserProfileFieldGroupStore groupStore;

	@Override
	protected String getSuccessMessage(boolean isNew) {
		return "User profile field has updated successfully";
	}

	@Override
	protected List<ValidationMessage> specificArgumentsValidation(ServiceRequest<UserProfileField> request) throws RuleValidationException {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		UserProfileField userProfileField = request.getData();
		UserProfileFieldModel sameUserProfile = store.findUserProfileFieldByRegistryNodeIdAndName(userProfileField.getParentId(), userProfileField.getName());
		if (sameUserProfile != null && !sameUserProfile.getId().equals(userProfileField.getId())) {
			validationMessages.add(new ValidationMessage("name", "user_profile_field.already.exist.with.name"));
		}
		if (userProfileField.getUserProfileFieldGroupId() != null) {
			UserProfileFieldGroupModel profileFieldGroup = groupStore.findUserProfileFieldGroupById(userProfileField.getUserProfileFieldGroupId(), false);
			if (profileFieldGroup == null) {
				ValidationMessage validationMessage = new ValidationMessage(null, "user_profile_field_group.not.exist");
				validationMessages.add(validationMessage);
			}
		}
		return validationMessages;
	}

	@Override
	protected UserProfileFieldModel convertToEntity(UserProfileField model) {
		return factory.convertFrom(UserProfileFieldModel.class, model);
	}

	@Override
	protected UserProfileFieldTree convertToModel(UserProfileFieldModel entity) {
		return factory.convertTo(UserProfileFieldTree.class, entity);
	}

	@Override
	protected void save(UserProfileFieldModel model) throws Exception {
		model.setChildCount(0);
		store.saveOrUpdate(model);
	}
}
