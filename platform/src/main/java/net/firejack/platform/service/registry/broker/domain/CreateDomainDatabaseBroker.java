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
