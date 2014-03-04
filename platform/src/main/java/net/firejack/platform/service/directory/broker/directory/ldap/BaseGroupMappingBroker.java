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

import net.firejack.platform.api.directory.domain.GroupMapping;
import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.broker.securedrecord.ISecuredRecordHandler;
import net.firejack.platform.core.broker.security.SecurityHandler;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.external.ldap.*;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDirectoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public abstract class BaseGroupMappingBroker extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<GroupMapping>> {

    public static final String PARAM_DIRECTORY_ID = "PARAM_DIRECTORY_ID";

    @Autowired
    @Qualifier("directoryStore")
    protected IDirectoryStore directoryStore;

    @Override
    protected ServiceResponse<GroupMapping> perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        Long directoryId = (Long) request.getData().get(PARAM_DIRECTORY_ID);
        DirectoryModel directoryModel = directoryStore.findById(directoryId);
        ServiceResponse<GroupMapping> response;
        if (directoryModel == null) {
            response = new ServiceResponse<GroupMapping>("Failed to find specified directory.", false);
        } else if (directoryModel.getDirectoryType() != DirectoryType.LDAP) {
            response = new ServiceResponse<GroupMapping>("Specified directory is not an LDAP directory", false);
        } else {
            DefaultLdapElementsAdaptor adaptor = new DefaultLdapElementsAdaptor();
            LdapSchemaConfig schemaConfig = LdapUtils.deSerializeConfig(directoryModel.getLdapSchemaConfig());
            adaptor.setSchemaConfig(schemaConfig);
            LdapServiceFacade ldapService = new LdapServiceFacade(
                    new ContextSourceContainer(directoryModel), adaptor);
            response = performInternal(directoryId, ldapService, request);
        }
        return response;
    }

    @Override
    protected SecurityHandler getSecurityHandler() {
        return null;
    }

    @Override
    protected ISecuredRecordHandler getSecuredRecordHandler() {
        return null;
    }

    protected abstract ServiceResponse<GroupMapping> performInternal(
            Long directoryId, LdapServiceFacade ldapService, ServiceRequest<NamedValues<Object>> request);

}