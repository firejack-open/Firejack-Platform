/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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
