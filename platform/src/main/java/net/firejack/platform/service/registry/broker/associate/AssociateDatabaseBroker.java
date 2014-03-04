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

package net.firejack.platform.service.registry.broker.associate;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDatabaseStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("associateDatabaseBroker")
public class AssociateDatabaseBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {
	@Autowired
	private IPackageStore packageStore;

	@Autowired
	private IDatabaseStore databaseStore;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> message) throws Exception {
		Long packageId = (Long) message.getData().get("packageId");
		Long databaseId = (Long) message.getData().get("databaseId");

		PackageModel packageModel = packageStore.findById(packageId);
		if (packageModel == null) {
			throw new BusinessFunctionException("Package ID parameter should not be null", "packageId");
		}
        DatabaseModel databaseModel = databaseStore.findById(databaseId);
		if (databaseModel == null) {
			throw new BusinessFunctionException("Database ID parameter should not be null", "databaseId");
		}

		packageModel.setDatabase(databaseModel);
        packageStore.saveOrUpdate(packageModel);
		return new ServiceResponse("Database has been associated successfully.", true);
	}
}
