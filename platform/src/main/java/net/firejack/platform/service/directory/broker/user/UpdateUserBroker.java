/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
