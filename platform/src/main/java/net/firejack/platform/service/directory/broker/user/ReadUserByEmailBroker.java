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
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@TrackDetails
@Component("readUserByEmailBroker")
public class ReadUserByEmailBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<User>> {

	@Autowired
	private IUserStore store;

	@Override
	protected ServiceResponse<User> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
		String email = request.getData().getIdentifier();
		if (email != null) {
			try {
				email = URLDecoder.decode(email, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.debug(e.getMessage(), e);
			}
			UserModel user = store.findUserByEmail(email);
			if (user != null) {
				user.setPassword(null);
				User dto = factory.convertTo(User.class, user);
				return new ServiceResponse<User>(dto, "Read User by Email successfully", true);
			}
		}
		return new ServiceResponse<User>("Can't find user with email " + email, false);
	}
}
