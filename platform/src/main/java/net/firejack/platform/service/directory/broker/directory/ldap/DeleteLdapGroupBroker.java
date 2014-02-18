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