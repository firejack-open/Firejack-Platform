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

package net.firejack.platform.service.directory.broker.directory.ldap;

import net.firejack.platform.api.directory.domain.Group;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.external.ldap.LdapServiceFacade;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;


@Component
@TrackDetails
public class CreateLdapGroupBroker extends BaseLdapBroker<Group> {

    public static final String PARAM_DIRECTORY_ID = "PARAM_DIRECTORY_ID";
    public static final String PARAM_GROUP = "PARAM_GROUP";

    @Override
    protected ServiceResponse<Group> performInternal(
            ServiceRequest<NamedValues<Object>> request, LdapServiceFacade ldapService) {
        Group group = (Group) request.getData().get(PARAM_GROUP);
        ServiceResponse<Group> response;
        try {
            ldapService.createGroup(group);
            response = new ServiceResponse<Group>(group, "Group created successfully.", true);
        } catch (Throwable th) {
            logger.error("Failed to create ldap group. Reason: " + th.getMessage(), th);
            response = new ServiceResponse<Group>(th.getMessage(), false);
        }
        return response;
    }

}