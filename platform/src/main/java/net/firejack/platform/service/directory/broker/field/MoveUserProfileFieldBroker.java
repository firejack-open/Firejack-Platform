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
