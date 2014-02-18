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