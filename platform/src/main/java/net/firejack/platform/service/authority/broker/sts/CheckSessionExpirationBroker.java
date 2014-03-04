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

package net.firejack.platform.service.authority.broker.sts;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.security.model.context.ContextLookupException;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.session.UserSessionManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;


@TrackDetails
@Component
public class CheckSessionExpirationBroker extends ServiceBroker
        <ServiceRequest, ServiceResponse<SimpleIdentifier<Boolean>>> {

    @Override
    protected ServiceResponse<SimpleIdentifier<Boolean>> perform(ServiceRequest request)
		    throws Exception {
        try {
            OPFContext context = OPFContext.getContext();
            Boolean expired = StringUtils.isBlank(context.getSessionToken()) ||
                    isSessionExpired(context.getSessionToken());
            return new ServiceResponse<SimpleIdentifier<Boolean>>(new SimpleIdentifier<Boolean>(expired), null, true);
        } catch (ContextLookupException e) {
            logger.error(e.getMessage());
            return new ServiceResponse<SimpleIdentifier<Boolean>>(
                    new SimpleIdentifier<Boolean>(true), e.getMessage(), true);
        }
    }

    private boolean isSessionExpired(String sessionToken) {
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        boolean result;
        if (sessionManager.isUserSessionExpired(sessionToken)) {
            result = true;//signal that session expired
        } else {
            sessionManager.prolongUserSessionExpiration(
                    sessionToken,  Long.parseLong(OpenFlameConfig.SESSION_EXPIRATION_PERIOD.getValue()));
            result = false;
        }
        return result;
    }

}