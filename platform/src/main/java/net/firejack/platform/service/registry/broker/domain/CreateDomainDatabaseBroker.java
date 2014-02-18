package net.firejack.platform.service.registry.broker.domain;

import net.firejack.platform.api.registry.domain.Database;
import net.firejack.platform.api.registry.domain.Domain;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDatabaseStore;
import net.firejack.platform.core.store.registry.IDomainStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.service.aop.annotation.Changes;
import net.firejack.platform.service.registry.broker.package_.ReverseEngineeringBroker;
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
@Component("createDomainDatabaseBroker")
@Changes("'New Domain '+ name +' with database has been created'")
public class CreateDomainDatabaseBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {

    @Autowired
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;
    @Autowired
    private IDatabaseStore databaseStore;
    @Autowired
    private IDomainStore domainStore;
    @Autowired
    private ReverseEngineeringBroker reverseEngineeringBroker;

    @Override
    protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
        Domain domain = (Domain) request.getData().get("domain");
        Boolean reverseEngineer = (Boolean) request.getData().get("reverseEngineer");

        Database database = domain.getDatabase();
        DatabaseModel databaseModel = factory.convertFrom(DatabaseModel.class, database);
        databaseStore.save(databaseModel);

        DomainModel domainModel = factory.convertFrom(DomainModel.class, domain);
        domainModel.setDatabase(databaseModel);

        if (domainModel.getParent() == null) {
            RegistryNodeModel registryNodeModel = registryNodeStore.findByLookup(domainModel.getPath());
            domainModel.setParent(registryNodeModel);
        }
        domainModel.setDataSource(reverseEngineer);
        domainStore.save(domainModel);

        ServiceResponse response;
        if (reverseEngineer) {
            NamedValues<Long> namedValues = new NamedValues<Long>();
            namedValues.put("registryNodeId", domainModel.getId());
            response = reverseEngineeringBroker.execute(new ServiceRequest<NamedValues>(namedValues));
        } else {
            response = new ServiceResponse("Database with Domain has been created.", true);
        }
        return response;
    }
}
