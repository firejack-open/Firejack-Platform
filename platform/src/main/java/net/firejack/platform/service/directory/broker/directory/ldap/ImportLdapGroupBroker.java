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
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.external.ldap.LdapServiceFacade;
import net.firejack.platform.core.model.registry.directory.GroupMappingModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IGroupMappingStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@TrackDetails
public class ImportLdapGroupBroker extends BaseGroupMappingBroker {

    public static final String PARAM_GROUP_DN = "PARAM_GROUP_DN";

    @Autowired
    @Qualifier("groupMappingStore")
    private IGroupMappingStore groupMappingStore;

    @Override
    protected ServiceResponse<GroupMapping> performInternal(
            Long directoryId, LdapServiceFacade ldapService, ServiceRequest<NamedValues<Object>> request) {
        String groupDN = (String) request.getData().get(PARAM_GROUP_DN);
        ServiceResponse<GroupMapping> response;
        if (StringUtils.isBlank(groupDN)) {
            response = new ServiceResponse<GroupMapping>("Group DN parameter should not be blank.", false);
        } else {
            GroupMappingModel groupMappingModel = groupMappingStore.importMapping(directoryId, groupDN);
            GroupMapping groupMapping = factory.convertTo(GroupMapping.class, groupMappingModel);
            response = new ServiceResponse<GroupMapping>(groupMapping, "Group Mapping was imported successfully", true);
        }
        return response;
    }

}