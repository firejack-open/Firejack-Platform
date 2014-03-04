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

import net.firejack.platform.api.directory.domain.UserProfile;
import net.firejack.platform.api.directory.domain.UserProfileField;
import net.firejack.platform.api.directory.domain.UserProfileFieldValue;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.model.user.UserProfileFieldModel;
import net.firejack.platform.core.model.user.UserProfileFieldValueModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.user.IUserProfileFieldStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@TrackDetails
public class LoadUserProfilesBroker extends ServiceBroker<ServiceRequest<NamedValues<Object>>, ServiceResponse<UserProfile>> {

    public static final String PARAM_USER_ID_LIST = "PARAM_USER_ID_LIST";
    public static final String PARAM_USER_PROFILE_FIELD_LIST = "PARAM_USER_PROFILE_FIELD_LIST";

    @Autowired
    @Qualifier("userStore")
    private IUserStore userStore;

    @Autowired
    @Qualifier("userProfileFieldStore")
    private IUserProfileFieldStore profileFieldStore;

    @Override
    @SuppressWarnings("unchecked")
    protected ServiceResponse<UserProfile> perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        List<Long> userIdList = (List<Long>) request.getData().get(PARAM_USER_ID_LIST);
        ServiceResponse<UserProfile> response;
        if (userIdList == null || userIdList.isEmpty()) {
            response = new ServiceResponse<UserProfile>("No user id information were specified.", false);
        } else {
            List<Criterion> criterionList = new ArrayList<Criterion>();
            criterionList.add(Restrictions.in("id", userIdList));
            List<UserModel> correspondingUsers = userStore.findAllWithFilter(criterionList, null);
            List<UserProfile> userProfiles = factory.convertTo(UserProfile.class, correspondingUsers);
            if (userProfiles.isEmpty()) {
                response = new ServiceResponse<UserProfile>("No corresponded users were found.", false);
            } else {
                List<UserProfileField> userProfileFields = (List<UserProfileField>)
                        request.getData().get(PARAM_USER_PROFILE_FIELD_LIST);
                if (userProfileFields != null && !userProfileFields.isEmpty()) {
                    Set<Long> userProfileFieldIdSet = new HashSet<Long>();
                    for (UserProfileField userProfileField : userProfileFields) {
                        if (userProfileField.getId() == null) {
                            if (StringUtils.isNotBlank(userProfileField.getLookup())) {
                                UserProfileFieldModel profileField =
                                        profileFieldStore.findByLookup(userProfileField.getLookup());
                                if (profileField != null) {
                                    userProfileFieldIdSet.add(profileField.getId());
                                }
                            }
                        } else {
                            userProfileFieldIdSet.add(userProfileField.getId());
                        }
                    }
                    if (!userProfileFieldIdSet.isEmpty()) {
                        Map<Long, List<UserProfileFieldValueModel>> profileFieldValueModels =
                                profileFieldStore.findUserProfileFieldValues(userIdList, userProfileFieldIdSet);
                        if (profileFieldValueModels != null) {
                            for (UserProfile userProfile : userProfiles) {
                                List<UserProfileFieldValueModel> userProfileFieldValueModels =
                                        profileFieldValueModels.get(userProfile.getUserId());
                                userProfile.setFieldValues(factory.convertTo(
                                        UserProfileFieldValue.class, userProfileFieldValueModels));
                            }
                        }
                    }
                }
                response = new ServiceResponse<UserProfile>(userProfiles, "User Profiles were loaded successfully", true);
            }
        }
        return response;
    }

}