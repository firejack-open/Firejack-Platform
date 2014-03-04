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

import net.firejack.platform.api.directory.domain.Directory;
import net.firejack.platform.api.directory.domain.Group;
import net.firejack.platform.api.directory.domain.GroupMapping;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.external.ldap.LdapServiceFacade;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IGroupMappingStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@TrackDetails
public class UpdateGroupMappingsBroker extends BaseGroupMappingBroker {

    public static final String PARAM_DIRECTORY_ID = "PARAM_DIRECTORY_ID";
    public static final String PARAM_GROUP_DN = "PARAM_GROUP_DN";
    public static final String PARAM_GROUP_MAPPINGS = "PARAM_GROUP_MAPPINGS";

    @Autowired
    @Qualifier("groupMappingStore")
    private IGroupMappingStore groupMappingStore;

    protected ServiceResponse<GroupMapping> performInternal(
            Long directoryId, LdapServiceFacade ldapService, ServiceRequest<NamedValues<Object>> request) {
        ServiceResponse<GroupMapping> response;
        String groupDN = (String) request.getData().get(PARAM_GROUP_DN);
        if (StringUtils.isBlank(groupDN)) {
            response = new ServiceResponse<GroupMapping>("LDAP groupDN should not be blank.", false);
        } else {
            @SuppressWarnings("unchecked")
            List<GroupMapping> groupMappings = (List<GroupMapping>) request.getData().get(PARAM_GROUP_MAPPINGS);
            if (groupMappings == null || groupMappings.isEmpty()) {
                response = new ServiceResponse<GroupMapping>("Nothing to update.", false);
            } else {
                String ldapGroupName = ldapService.getLastEntryValueFromDN(groupDN).getValue();
                try {
                    Group ldapGroup = ldapService.findGroupByName(ldapGroupName);
                    if (ldapGroup == null) {
                        response = new ServiceResponse<GroupMapping>(
                                "Failed to update mapping for ldap group because specified group was not found in LDAP.", false);
                    } else {
                        Map<Long, Group> groupsByIdMap = new HashMap<Long, Group>();
                        for (GroupMapping groupMapping : groupMappings) {
                            groupsByIdMap.put(groupMapping.getGroup().getId(), groupMapping.getGroup());
                        }
                        groupMappingStore.updateGroupMappingsForLdapGroup(directoryId, groupDN, groupsByIdMap.keySet());
                        List<GroupMapping> mappings = new ArrayList<GroupMapping>(groupMappings.size());
                        for (GroupMapping groupMapping : groupMappings) {
                            Directory directory = new Directory();
                            directory.setId(directoryId);
                            GroupMapping mapping = new GroupMapping();
                            mapping.setGroup(groupMapping.getGroup());
                            mapping.setDirectory(directory);
                            mapping.setLdapGroupDN(groupDN);
                            mappings.add(mapping);
                        }
                        response = new ServiceResponse<GroupMapping>(
                                mappings, "Mappings for LDAP group have successfully updated.", true);
                    }
                } catch (Throwable th) {
                    logger.error("Failed to update mapping for ldap group. Reason: " + th.getMessage(), th);
                    response = new ServiceResponse<GroupMapping>(th.getMessage(), false);
                }
            }
        }

        return response;
    }

}