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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.Environment;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.model.registry.system.ServerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class EnvironmentStore implements IEnvironmentStore {
	@Autowired
	@Qualifier("packageStore")
	private IPackageStore packageStore;
	@Autowired
	@Qualifier("systemStore")
	private ISystemStore systemStore;
	@Autowired
	@Qualifier("databaseStore")
	private IDatabaseStore databaseStore;
	@Autowired
	@Qualifier("serverStore")
	private IServerStore serverStore;
	@Autowired
	@Qualifier("fileStore")
	private IFileStore fileStore;

	@Transactional
	public void save(RootDomainModel domain, Environment environment) {
		SystemModel system = environment.getSystem();
		List<PackageModel> packages = system.getAssociatedPackages();
		List<DatabaseModel> databases = environment.getDatabases();
		List<ServerModel> servers = environment.getServers();
		List<FileStoreModel> filestores = environment.getFilestores();

		system.setAssociatedPackages(null);

		save(systemStore, system, domain);
		save(databaseStore, databases, system);
		save(serverStore, servers, system);
		save(fileStore, filestores, system);

		if (packages != null) {
			for (PackageModel aPackage : packages) {
				String lookup = aPackage.getLookup();
				if (lookup.startsWith(domain.getLookup())) {
					PackageModel pkg = packageStore.findByLookup(lookup);
					if (pkg != null) {
						packageStore.associate(pkg, system);
					}
				}
			}
		}
	}

	private <T extends RegistryNodeModel> void save(IRegistryNodeStore<T> store, List<T> nodes, RegistryNodeModel parent) {
		if (nodes != null) {
			for (T node : nodes) {
				save(store, node, parent);
			}
		}
	}

	private <T extends RegistryNodeModel> void save(IRegistryNodeStore<T> store, T node, RegistryNodeModel parent) {
		String lookup = DiffUtils.lookup(node.getPath(), node.getName());
		T model = store.findByLookup(lookup);
		if (model != null) {
			node.setId(model.getId());
			node.setHash(model.getHash());
			node.setUid(model.getUid());
			store.evict(model);
		}

		node.setParent(parent);
		store.save(node);
	}
}
