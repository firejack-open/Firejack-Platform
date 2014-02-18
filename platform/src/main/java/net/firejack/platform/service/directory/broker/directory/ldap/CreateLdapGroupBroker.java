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
public class CreateLdapGroupBroker extends BaseLdapBroker<Group> {

    public static final String PARAM_DIRECTORY_ID = "PARAM_DIRECTORY_ID";
    public static final String PARAM_GROUP = "PARAM_GROUP";

    @Override
    protected ServiceResponse<Group> performInternal(
            ServiceRequest<NamedValues<Object>> request, LdapServiceFacade ldapService) {
        Group group = (Group) request.getData().get(PARAM_GROUP);
        ServiceResponse<Group> response;
        try {
            ldapService.createGroup(group);
            response = new ServiceResponse<Group>(group, "Group created successfully.", true);
        } catch (Throwable th) {
            logger.error("Failed to create ldap group. Reason: " + th.getMessage(), th);
            response = new ServiceResponse<Group>(th.getMessage(), false);
        }
        return response;
    }

}