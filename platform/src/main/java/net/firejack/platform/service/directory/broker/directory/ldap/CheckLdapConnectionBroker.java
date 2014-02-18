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