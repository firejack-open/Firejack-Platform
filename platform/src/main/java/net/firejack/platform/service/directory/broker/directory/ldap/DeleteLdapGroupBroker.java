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
public class DeleteLdapGroupBroker extends BaseLdapBroker<Group> {

    public static final String PARAM_GROUP_NAME = "PARAM_GROUP_NAME";

    @Override
    protected ServiceResponse<Group> performInternal(
            ServiceRequest<NamedValues<Object>> request, LdapServiceFacade ldapService) {
        ServiceResponse<Group> response;
        try {
            String groupName = (String) request.getData().get(PARAM_GROUP_NAME);
            Group Group = ldapService.findGroupByName(groupName);
            if (Group == null) {
                response = new ServiceResponse<Group>(
                        "Failed to find Group in LDAP directory for delete operation.", false);
            } else {
                ldapService.deleteGroup(Group);
                response = new ServiceResponse<Group>(Group, "Group has deleted successfully.", true);
            }
        } catch (Throwable e) {
            logger.error("Failed to create ldap Group. Reason: " + e.getMessage(), e);
            response = new ServiceResponse<Group>(e.getMessage(), false);
        }
        return response;
    }

}