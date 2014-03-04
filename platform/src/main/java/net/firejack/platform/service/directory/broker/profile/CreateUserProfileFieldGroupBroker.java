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
