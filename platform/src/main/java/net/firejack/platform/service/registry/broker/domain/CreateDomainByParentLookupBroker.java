package net.firejack.platform.service.registry.broker.domain;

import net.firejack.platform.api.registry.domain.Domain;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDomainStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.service.aop.annotation.Changes;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@TrackDetails
@Component("createDomainByLookupBroker")
@Changes("'New Domain '+ name +' has been created'")
public class CreateDomainByParentLookupBroker extends ServiceBroker<ServiceRequest<Domain>, ServiceResponse<Domain>> {

    @Autowired
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;
    @Autowired
    private IDomainStore domainStore;

    @Override
    protected ServiceResponse<Domain> perform(ServiceRequest<Domain> request) throws Exception {
        Domain domain = request.getData();

        RegistryNodeModel registryNodeModel = registryNodeStore.findByLookup(domain.getPath());

        DomainModel domainModel = factory.convertFrom(DomainModel.class, domain);
        domainModel.setParent(registryNodeModel);
        domainStore.save(domainModel);

        domain = factory.convertTo(Domain.class, domainModel);

        return new ServiceResponse<Domain>(domain, "New Domain has been created.", true);
    }

}
