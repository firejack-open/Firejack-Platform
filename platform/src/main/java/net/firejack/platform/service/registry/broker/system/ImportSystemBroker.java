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

package net.firejack.platform.service.registry.broker.system;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.Environments;
import net.firejack.platform.core.model.registry.domain.Environment;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.EnvironmentStore;
import net.firejack.platform.core.store.registry.IRootDomainStore;
import net.firejack.platform.core.store.registry.ISystemStore;
import net.firejack.platform.core.utils.EnvironmentsUtils;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("importSystemBroker")
@ProgressComponent(upload = true)
public class ImportSystemBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {
	@Autowired
	private IRootDomainStore rootDomainRegistryNodeStore;
	@Autowired
	private ISystemStore systemIRegistryNodeStore;
	@Autowired
	private EnvironmentStore environmentStore;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
		Long id = (Long) request.getData().get("rootDomainId");
		InputStream inputStream = (InputStream) request.getData().get("stream");

		Environments environments = EnvironmentsUtils.deserialize(inputStream);
		if (environments != null && !environments.isEmpty()) {
			List<RootDomainModel> domains = new ArrayList<RootDomainModel>();
			if (id != null) {
				RootDomainModel domain = rootDomainRegistryNodeStore.findById(id);
				domains.add(domain);
			} else {
				domains = rootDomainRegistryNodeStore.findAll();
			}
			changeSystem(domains, environments);
		}

		return new ServiceResponse("Import xml successfully", true);
	}

	private void changeSystem(List<RootDomainModel> domains, Environments<Environment> environments) {
		for (Environment environment : environments.getEnvironments()) {
			for (RootDomainModel domain : domains) {
				String lookup = DiffUtils.lookup(environment.getSystem().getPath(), environment.getSystem().getName());
				if (lookup.startsWith(domain.getLookup())) {
					SystemModel system = systemIRegistryNodeStore.findByLookup(lookup);
					if (system != null) {
						systemIRegistryNodeStore.deleteRecursiveById(system.getId());
					}
					environmentStore.save(domain, environment);
				}
			}
		}
	}
}
