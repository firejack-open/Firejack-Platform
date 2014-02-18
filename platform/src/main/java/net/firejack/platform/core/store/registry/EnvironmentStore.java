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
