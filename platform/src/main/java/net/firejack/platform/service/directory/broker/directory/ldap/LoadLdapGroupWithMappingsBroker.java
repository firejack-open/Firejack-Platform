/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.service.directory.broker.directory.ldap;

import net.firejack.platform.api.directory.domain.Group;
import net.firejack.platform.api.directory.domain.GroupMapping;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.external.ldap.LdapServiceFacade;
import net.firejack.platform.core.model.registry.directory.GroupMappingModel;
import net.firejack.platform.core.model.registry.directory.GroupModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IGroupMappingStore;
import net.firejack.platform.core.store.registry.IGroupStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@TrackDetails
@Component
public class LoadLdapGroupWithMappingsBroker extends BaseGroupMappingBroker {

    public static final String PARAM_GROUP_DN = "PARAM_GROUP_DN";

    @Autowired
    @Qualifier("groupMappingStore")
    private IGroupMappingStore groupMappingStore;

    @Autowired
    @Qualifier("groupStore")
    private IGroupStore groupStore;

    @Override
    protected ServiceResponse<GroupMapping> performInternal(
            Long directoryId, LdapServiceFacade ldapService, ServiceRequest<NamedValues<Object>> request) {
        ServiceResponse<GroupMapping> response;
        String groupDN = (String) request.getData().get(PARAM_GROUP_DN);
        if (StringUtils.isBlank(groupDN)) {
            response = new ServiceResponse<GroupMapping>("Group name should not be blank.", false);
        } else {
            try {
                String ldapGroupName = ldapService.getLastEntryValueFromDN(groupDN).getValue();
                Group loadedGroup = ldapService.findGroupByName(ldapGroupName);
                if (loadedGroup == null) {
                    response = new ServiceResponse<GroupMapping>("No group found for specified group name.", false);
                } else {
                    List<GroupMappingModel> groupMappingModels =
                            groupMappingStore.loadGroupMappingsForLdapGroup(directoryId, groupDN);
                    List<GroupMapping> groupMappings = factory.convertTo(GroupMapping.class, groupMappingModels);
                    List<Long> groupIdList = new ArrayList<Long>();
                    if (groupMappings != null) {
                        for (GroupMapping groupMapping : groupMappings) {
                            groupIdList.add(groupMapping.getGroup().getId());
                        }
                    }
                    List<GroupModel> availableGroupModels = groupStore.findAll(groupIdList);
                    List<Group> availableGroups = factory.convertTo(Group.class, availableGroupModels);
                    GroupMapping fakeGroupMapping = new GroupMapping();
                    fakeGroupMapping.setAvailableGroups(availableGroups);
                    List<GroupMapping> result = new ArrayList<GroupMapping>();
                    result.add(fakeGroupMapping);
                    String message;
                    if (groupMappings == null) {
                        message = "Group mappings were found";
                    } else {
                        result.addAll(groupMappings);
                        message = "No group mappings were found";
                    }
                    response = new ServiceResponse<GroupMapping>(result, message, true);
                }
            } catch (Throwable e) {
                logger.error("Failed to load ldap group with roles. Reason: " + e.getMessage(), e);
                response = new ServiceResponse<GroupMapping>(e.getMessage(), false);
            }
        }
        return response;
    }

}