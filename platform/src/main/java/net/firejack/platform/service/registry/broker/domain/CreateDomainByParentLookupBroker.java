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
