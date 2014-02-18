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
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.user.IBaseUserStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.core.validation.ValidationMessage;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.service.directory.broker.UpdateBaseUserBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("updateUserBroker")
public class UpdateUserBroker extends UpdateBaseUserBroker<UserModel, User> {

	@Autowired
    @Qualifier("userStore")
	private IUserStore store;

    @Override
    protected IBaseUserStore<UserModel> getStore() {
        return store;
    }

    @Override
    protected List<ValidationMessage> specificArgumentsValidation(ServiceRequest<User> request) throws RuleValidationException {
        List<ValidationMessage> validationMessages = super.specificArgumentsValidation(request);

        User user = request.getData();
        if (user.getPassword() != null && !user.getPassword().equals(user.getPasswordConfirm())) {
			ValidationMessage validationMessage = new ValidationMessage(null, "user.password.confirm.incorrect");
			validationMessages.add(validationMessage);
		}
        return validationMessages;
    }

    @Override
	protected void processArguments(ServiceRequest<User> request) {
        User userVO = request.getData();
        UserModel user = getStore().findById(userVO.getId());
        userVO.setFacebookId(user.getFacebookId());
	    if (StringUtils.isBlank(userVO.getPassword())) {
		    userVO.setPassword(user.getPassword());
		    userVO.setPasswordConfirm(user.getPassword());
	    } else {
		    String hashPassword = SecurityHelper.hash(userVO.getPassword());
		    userVO.setPassword(hashPassword);
		    userVO.setPasswordConfirm(hashPassword);
	    }
    }

    @Override
	protected String getSuccessMessage(boolean isNew) {
		return "User has updated successfully";
	}

	@Override
	protected UserModel convertToEntity(User model) {
        UserModel userModel = factory.convertFrom(UserModel.class, model);
        if (userModel.getUserRoles() != null) {
            for (UserRoleModel userRole : userModel.getUserRoles()) {
                userRole.setUser(userModel);
            }
        }
        return userModel;
	}

	@Override
	protected User convertToModel(UserModel entity) {
		return factory.convertTo(User.class, entity);
	}

}
