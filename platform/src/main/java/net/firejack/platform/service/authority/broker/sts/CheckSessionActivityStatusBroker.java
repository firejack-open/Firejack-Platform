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
import net.firejack.platform.core.broker.workflow.IWorkflowHandler;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.cache.CacheManager;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;


@TrackDetails
@Component
public class CheckSessionActivityStatusBroker extends ServiceBroker
        <ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<SimpleIdentifier<Boolean>>> {

    @Override
    protected ServiceResponse<SimpleIdentifier<Boolean>> perform(
            ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        String sessionToken = request.getData().getIdentifier();
        Boolean sessionTokenActiveStatus;
        if (StringUtils.isBlank(sessionToken)) {
            sessionTokenActiveStatus = false;
        } else {
            sessionTokenActiveStatus = CacheManager.getInstance()
                    .getSessionTokenActiveStatus(sessionToken);
            sessionTokenActiveStatus = sessionTokenActiveStatus == null ? false : sessionTokenActiveStatus;
        }
        return new ServiceResponse<SimpleIdentifier<Boolean>>(
                new SimpleIdentifier<Boolean>(sessionTokenActiveStatus), null, true);
    }

    @Override
    public IWorkflowHandler getWorkflowHandler() {
        return null;
    }
}