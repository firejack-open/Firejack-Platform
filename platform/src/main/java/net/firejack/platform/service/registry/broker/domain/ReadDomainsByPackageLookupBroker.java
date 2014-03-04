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
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.DomainModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.IDomainStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("readDomainsByPackageLookupBroker")
public class ReadDomainsByPackageLookupBroker extends ListBroker<DomainModel, Domain, SimpleIdentifier<String>> {

    @Autowired
    private IPackageStore packageStore;
    @Autowired
	private IDomainStore domainStore;

    @Override
    protected List<DomainModel> getModelList(ServiceRequest<SimpleIdentifier<String>> request) throws BusinessFunctionException {
        String packageLookup = request.getData().getIdentifier();
        if (packageLookup == null) {
            throw new BusinessFunctionException("Package Lookup can't be empty.");
        }

        PackageModel packageModel = packageStore.findPackage(packageLookup);
        if (packageModel == null) {
            throw new BusinessFunctionException("Could not find Package by lookup: " + packageLookup);
        }

        Class[] registryNodeClasses = {
            RegistryNodeType.DOMAIN.getClazz()
        };

        return domainStore.findAllByPrefixLookupAndTypes(packageLookup, getFilter(), registryNodeClasses);
    }
}