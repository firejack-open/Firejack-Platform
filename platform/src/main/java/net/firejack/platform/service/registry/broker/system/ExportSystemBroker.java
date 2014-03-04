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

import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.Environments;
import net.firejack.platform.core.model.registry.domain.Environment;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.*;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component("exportSystemBroker")
public class ExportSystemBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<FileInfo>> {
	@Autowired
	private IRootDomainStore rootDomainRegistryNodeStore;
	@Autowired
	private ISystemStore systemRegistryNodeStore;
	@Autowired
	private IDatabaseStore databaseRegistryNodeStore;
	@Autowired
	private IServerStore serverRegistryNodeStore;
	@Autowired
	private IFileStore filestoreRegistryNodeStore;

	@Override
	protected ServiceResponse<FileInfo> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
		Long rootDomainId = request.getData().getIdentifier();

		List<Long> ids = new ArrayList<Long>();
		if (rootDomainId != null) {
			ids.add(rootDomainId);
		} else {
			List<RootDomainModel> all = rootDomainRegistryNodeStore.findAll();
			for (RootDomainModel domain : all) {
				ids.add(domain.getId());
			}
		}

		List<SystemModel> systems = systemRegistryNodeStore.findAllByParentIdsWithFilter(ids, null, null);
		FileInfo fileInfo = new FileInfo();
		if (systems != null) {
			List<Environment> environments = new ArrayList<Environment>();

			for (SystemModel system : systems) {
				List<DatabaseModel> databases = databaseRegistryNodeStore.findAllByParentIdWithFilter(system.getId(), null);
				List<ServerModel> servers = serverRegistryNodeStore.findAllByParentIdWithFilter(system.getId(), null);
				List<FileStoreModel> filestores = filestoreRegistryNodeStore.findAllByParentIdWithFilter(system.getId(), null);

				Environment environment = new Environment();
				environment.setSystem(system);
				environment.setDatabases(databases);
				environment.setServers(servers);
				environment.setFilestores(filestores);
				environments.add(environment);
			}

			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				FileUtils.writeJAXB(new Environments(environments), out, Environments.class, Environment.class);

				fileInfo.setStream(new ByteArrayInputStream(out.toByteArray()));
				IOUtils.closeQuietly(out);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} catch (JAXBException e) {
				logger.error(e.getMessage(), e);
			}
		}

		return new ServiceResponse<FileInfo>(fileInfo, null, true);
	}
}
