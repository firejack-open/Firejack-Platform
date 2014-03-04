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

package net.firejack.platform.service.deployment.broker;


import net.firejack.platform.api.deployment.domain.PackageChange;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.domain.PackageChangesModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageChangesStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component
public class PackageChangesBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<String>>, ServiceResponse<PackageChange>> {

    @Autowired
    private IPackageChangesStore changesStore;

    @Override
    protected ServiceResponse<PackageChange> perform(ServiceRequest<SimpleIdentifier<String>> request) throws Exception {
        String packageLookup = request.getData().getIdentifier();
        List<PackageChangesModel> changes = changesStore.findAllPackageChange(packageLookup);
        List<PackageChange> packageChanges = factory.convertTo(PackageChange.class, changes);
        return new ServiceResponse<PackageChange>(packageChanges, "Read package changes successfully", true);
    }
}
