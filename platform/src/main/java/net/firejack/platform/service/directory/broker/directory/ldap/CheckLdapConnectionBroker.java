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

import net.firejack.platform.api.registry.domain.CheckUrl;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.broker.securedrecord.ISecuredRecordHandler;
import net.firejack.platform.core.broker.security.SecurityHandler;
import net.firejack.platform.core.external.ldap.*;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;


@Component
@TrackDetails
public class CheckLdapConnectionBroker extends ServiceBroker<ServiceRequest<CheckUrl>, ServiceResponse<CheckUrl>> {

    @Override
    protected ServiceResponse<CheckUrl> perform(ServiceRequest<CheckUrl> request) throws Exception {
        CheckUrl checkUrl = request.getData();
        String message;
        if (StringUtils.isBlank(checkUrl.getServerName()) || checkUrl.getPort() == null ||
                StringUtils.isBlank(checkUrl.getUrlPath()) || StringUtils.isBlank(checkUrl.getUsername())) {
            checkUrl.setStatus(RegistryNodeStatus.INACTIVE.name());
            message = "Not enough information to obtain LDAP connection.";
        } else {
            try {
                DefaultLdapElementsAdaptor adaptor = new DefaultLdapElementsAdaptor();
                LdapSchemaConfig schemaConfig = LdapUtils.deSerializeConfig(checkUrl.getLdapSchemaConfig());
                adaptor.setSchemaConfig(schemaConfig);
                LdapServiceFacade ldapService = new LdapServiceFacade(
                        new ContextSourceContainer(checkUrl), adaptor);
                ldapService.searchUsers("any");
                checkUrl.setStatus(RegistryNodeStatus.ACTIVE.name());
                message = "Connection information is correct.";
            } catch (Throwable e) {
                logger.error("Failed to check ldap connection. Reason: " + e.getMessage(), e);
                checkUrl.setStatus(RegistryNodeStatus.INACTIVE.name());
                message = "Connection information is incorrect.";
            }
        }
        return new ServiceResponse<CheckUrl>(checkUrl, message, true);
    }

    @Override
    protected SecurityHandler getSecurityHandler() {
        return null;
    }

    @Override
    protected ISecuredRecordHandler getSecuredRecordHandler() {
        return null;
    }

}