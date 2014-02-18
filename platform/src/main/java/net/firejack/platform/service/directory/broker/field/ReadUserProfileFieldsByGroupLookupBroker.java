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

import net.firejack.platform.api.directory.domain.UserProfileField;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.user.UserProfileFieldModel;
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
public class ReadUserProfileFieldsByGroupLookupBroker extends ServiceBroker
        <ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<UserProfileField>> {

    @Autowired
    @Qualifier("userProfileFieldStore")
    private IUserProfileFieldStore profileFieldStore;

    @Override
    protected ServiceResponse<UserProfileField> perform(ServiceRequest<SimpleIdentifier<String>> request)
            throws Exception {
        String groupLookup = request.getData().getIdentifier();
        List<UserProfileFieldModel> profileFieldModels =
                profileFieldStore.findGroupedProfileFields(groupLookup);
        List<UserProfileField> profileFields =
                factory.convertTo(UserProfileField.class, profileFieldModels);
        return profileFields == null ?
                new ServiceResponse<UserProfileField>("No User Profile Fields were found", true) :
                new ServiceResponse<UserProfileField>(profileFields, "User Profile Fields were found", true);
    }

}