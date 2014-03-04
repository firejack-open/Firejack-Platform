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
public class CreateUserProfileFieldsValuesBroker extends ServiceBroker
        <ServiceRequest<UserProfileFieldValue>, ServiceResponse<UserProfileFieldValue>> {

    @Autowired
    @Qualifier("userProfileFieldStore")
    private IUserProfileFieldStore profileFieldStore;

    @Override
    protected ServiceResponse<UserProfileFieldValue> perform(ServiceRequest<UserProfileFieldValue> request)
            throws Exception {
        List<UserProfileFieldValue> fieldValues = request.getDataList();
        ServiceResponse<UserProfileFieldValue> response;
        if (fieldValues == null || fieldValues.isEmpty()) {
            response = new ServiceResponse<UserProfileFieldValue>("No user profile field values to create.", false);
        } else {
            List<UserProfileFieldValueModel> valueModels =
                    factory.convertFrom(UserProfileFieldValueModel.class, fieldValues);
            valueModels = profileFieldStore.saveUserProfileFieldModelList(valueModels);
            List<UserProfileFieldValue> responseData = factory.convertTo(UserProfileFieldValue.class, valueModels);
            response = responseData == null ?
                    new ServiceResponse<UserProfileFieldValue>("No user profile field values were saved.", false) :
                    new ServiceResponse<UserProfileFieldValue>(
                            responseData, "User Profile Field values were saved successfully.", true);
        }
        return response;
    }

}