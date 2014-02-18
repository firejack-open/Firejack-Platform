/**
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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
