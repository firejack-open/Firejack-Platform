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
