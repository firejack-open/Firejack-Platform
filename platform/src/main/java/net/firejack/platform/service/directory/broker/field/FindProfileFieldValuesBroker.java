/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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