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
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@TrackDetails
public class SearchLdapGroupsBroker extends BaseLdapBroker<Group> {

    public static final String PARAM_SEARCH_TERM = "PARAM_SEARCH_TERM";

    @Override
    protected ServiceResponse<Group> performInternal(ServiceRequest<NamedValues<Object>> request, LdapServiceFacade ldapService) {
        ServiceResponse<Group> response;
        try {
            String searchTerm = (String) request.getData().get(PARAM_SEARCH_TERM);
            List<Group> foundGroups = StringUtils.isBlank(searchTerm) ?
                    ldapService.findAllGroups() : ldapService.searchGroups(searchTerm);
            response = foundGroups == null || foundGroups.isEmpty() ?
                    new ServiceResponse<Group>("No groups found for specified search criteria.", true) :
                    new ServiceResponse<Group>(foundGroups, "Groups were found for specified search criteria.", true);
        } catch (Throwable e) {
            logger.error("Failed to update ldap group. Reason: " + e.getMessage(), e);
            response = new ServiceResponse<Group>(e.getMessage(), false);
        }
        return response;
    }

}