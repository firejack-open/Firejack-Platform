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
import net.firejack.platform.api.directory.domain.UserIdentity;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("readUserIdentityBroker")
public class ReadUserIdentityBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<UserIdentity>> {

	@Override
	protected ServiceResponse<UserIdentity> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        String sessionToken = request.getData().getIdentifier();
        ServiceResponse<UserIdentity> response;
		if (StringUtils.isBlank(sessionToken)) {
			response = new ServiceResponse<UserIdentity>("Guest tries to load protected info.", false);
		} else {
			Tuple<User, List<Long>> userInfo = CacheManager.getInstance().getUserInfo(sessionToken);
            if (userInfo == null) {
                response = new ServiceResponse<UserIdentity>("Not detected user tries to load protected info.", true);
            } else {
                User user = userInfo.getKey();
                List<Long> roleIdList = userInfo.getValue();

                UserIdentity vo = new UserIdentity();
                vo.setId(user.getId());
                vo.setUserName(user.getUsername());
                vo.setFirstName(user.getFirstName());
                vo.setLastName(user.getLastName());
                vo.setRoleIds(roleIdList == null ? new Long[0] : roleIdList.toArray(new Long[roleIdList.size()]));

                response = new ServiceResponse<UserIdentity>(vo, "Read user identity successfully", true);
            }
		}
		return response;
	}
}
