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

import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.broker.securedrecord.ISecuredRecordHandler;
import net.firejack.platform.core.broker.security.SecurityHandler;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.external.ldap.*;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDirectoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public abstract class BaseLdapBroker<T extends BaseEntity> extends ServiceBroker
        <ServiceRequest<NamedValues<Object>>, ServiceResponse<T>> {

    public static final String PARAM_DIRECTORY_ID = "PARAM_DIRECTORY_ID";

    @Autowired
    @Qualifier("directoryStore")
    protected IDirectoryStore directoryStore;

    @Override
    protected ServiceResponse<T> perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
        Long directoryId = (Long) request.getData().get(PARAM_DIRECTORY_ID);
        DirectoryModel directoryModel = directoryStore.findById(directoryId);
        ServiceResponse<T> response;
        if (directoryModel == null) {
            response = new ServiceResponse<T>("Failed to find specified directory.", false);
        } else if (directoryModel.getDirectoryType() != DirectoryType.LDAP) {
            response = new ServiceResponse<T>("Specified directory is not an LDAP directory", false);
        } else {
            DefaultLdapElementsAdaptor adaptor = new DefaultLdapElementsAdaptor();
            LdapSchemaConfig schemaConfig = LdapUtils.deSerializeConfig(directoryModel.getLdapSchemaConfig());
            adaptor.setSchemaConfig(schemaConfig);
            LdapServiceFacade ldapService = new LdapServiceFacade(
                    new ContextSourceContainer(directoryModel), adaptor);
            response = performInternal(request, ldapService);
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

    protected abstract ServiceResponse<T> performInternal(
            ServiceRequest<NamedValues<Object>> request, LdapServiceFacade ldapService);

}