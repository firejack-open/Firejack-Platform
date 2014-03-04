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

import net.firejack.platform.api.directory.domain.UserProfileFieldValue;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.broker.securedrecord.ISecuredRecordHandler;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.user.UserProfileFieldValueModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.user.IUserProfileFieldStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@TrackDetails
public class FindProfileFieldValuesBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<UserProfileFieldValue>> {

    public static final String PARAM_USER_ID = "PARAM_USER_ID";
    public static final String PARAM_USER_PROFILE_FIELD_ID_LIST = "PARAM_USER_PROFILE_FIELD_ID_LIST";

    @Autowired
    @Qualifier("userProfileFieldStore")
    private IUserProfileFieldStore userProfileFieldStore;

    @Override
    protected ServiceResponse<UserProfileFieldValue> perform(ServiceRequest<NamedValues<Object>> request)
            throws Exception {
        NamedValues<Object> params = request.getData();
        Long userId = (Long) params.get(FindProfileFieldValuesBroker.PARAM_USER_ID);
        @SuppressWarnings("unchecked")
        List<Long> profileFieldIdList = (List<Long>) params.get(
                FindProfileFieldValuesBroker.PARAM_USER_PROFILE_FIELD_ID_LIST);
        ServiceResponse<UserProfileFieldValue> response;
        if (userId == null) {
            response = new ServiceResponse<UserProfileFieldValue>("userId parameter should not be null", false);
        } else if (profileFieldIdList == null || profileFieldIdList.isEmpty()) {
            response = new ServiceResponse<UserProfileFieldValue>(
                    "userProfileFieldIdList parameter should not be empty", false);
        } else {
            List<UserProfileFieldValueModel> fieldValueModels =
                    userProfileFieldStore.findUserProfileFieldValuesByUserAndFields(userId, profileFieldIdList);
            List<UserProfileFieldValue> responseData = factory.convertTo(UserProfileFieldValue.class, fieldValueModels);
            response = responseData == null ?
                    new ServiceResponse<UserProfileFieldValue>("No user profile field values were found.", true) :
                    new ServiceResponse<UserProfileFieldValue>(responseData, "User profile field values were found", true);
        }
        return response;
    }

    @Override
    protected ISecuredRecordHandler getSecuredRecordHandler() {
        return null;
    }
}