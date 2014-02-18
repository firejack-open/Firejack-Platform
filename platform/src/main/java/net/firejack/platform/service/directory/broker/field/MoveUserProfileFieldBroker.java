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

import net.firejack.platform.api.directory.domain.UserProfileFieldTree;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.model.user.UserProfileFieldModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
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
@Component("moveUserProfileFieldBroker")
public class MoveUserProfileFieldBroker
        extends ServiceBroker<ServiceRequest<NamedValues<Long>>, ServiceResponse<UserProfileFieldTree>> {

	@Autowired
    @Qualifier("userProfileFieldStore")
	private IUserProfileFieldStore store;
	@Autowired
    @Qualifier("userProfileFieldGroupStore")
	protected IUserProfileFieldGroupStore groupStore;

    private UserProfileFieldModel userProfileFieldModel;
    private UserProfileFieldGroupModel userProfileFieldGroupModel;

    @Override
	protected List<ValidationMessage> specificArgumentsValidation(ServiceRequest<NamedValues<Long>> request) throws RuleValidationException {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		NamedValues<Long> parameters = request.getData();
        Long userProfileFieldId = parameters.get("userProfileFieldId");
        Long userProfileFieldGroupId = parameters.get("userProfileFieldGroupId");

		userProfileFieldModel = store.findById(userProfileFieldId);
		if (userProfileFieldModel == null) {
			validationMessages.add(new ValidationMessage(null, "object.not.found"));
		}

        userProfileFieldGroupModel = groupStore.findUserProfileFieldGroupById(userProfileFieldGroupId, false);
        if (userProfileFieldGroupModel == null) {
            ValidationMessage validationMessage = new ValidationMessage(null, "user_profile_field_group.not.exist");
            validationMessages.add(validationMessage);
        }
		return validationMessages;
	}

    @Override
    protected ServiceResponse<UserProfileFieldTree> perform(ServiceRequest<NamedValues<Long>> request) throws Exception {
        userProfileFieldModel.setUserProfileFieldGroup(userProfileFieldGroupModel);
        store.saveOrUpdate(userProfileFieldModel);
        UserProfileFieldTree userProfileFieldTree = factory.convertTo(UserProfileFieldTree.class, userProfileFieldModel);
        ServiceResponse<UserProfileFieldTree> response = new ServiceResponse<UserProfileFieldTree>("Group '" + userProfileFieldGroupModel.getName() + "' has been moved successfully.", true);
        response.addItem(userProfileFieldTree);
        return response;
    }

}
