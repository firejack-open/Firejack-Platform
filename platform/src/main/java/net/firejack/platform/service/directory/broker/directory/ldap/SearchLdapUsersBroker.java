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

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.external.ldap.LdapServiceFacade;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@Transactional
public class SearchLdapUsersBroker extends BaseLdapBroker<User> {

    public static final String PARAM_SEARCH_TERM = "PARAM_SEARCH_TERM";

    @Override
    protected ServiceResponse<User> performInternal(
            ServiceRequest<NamedValues<Object>> request, LdapServiceFacade ldapService) {
        ServiceResponse<User> response;
        try {
            String searchTerm = (String) request.getData().get(PARAM_SEARCH_TERM);
            List<User> foundUsers = StringUtils.isBlank(searchTerm) ?
                    ldapService.getAllUsers() : ldapService.searchUsers(searchTerm);
            response = foundUsers == null || foundUsers.isEmpty() ?
                    new ServiceResponse<User>("No users found for specified search criteria.", true) :
                    new ServiceResponse<User>(foundUsers, "Users were found for specified search criteria.", true);
        } catch (Throwable e) {
            logger.error("Failed to update ldap user. Reason: " + e.getMessage(), e);
            response = new ServiceResponse<User>(e.getMessage(), false);
        }
        return response;
    }

}