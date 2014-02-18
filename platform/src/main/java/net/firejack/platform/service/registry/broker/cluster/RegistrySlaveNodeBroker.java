package net.firejack.platform.service.registry.broker.cluster;

import com.sun.jersey.core.util.Base64;
import net.firejack.platform.api.registry.domain.ServerNodeConfig;
import net.firejack.platform.api.registry.model.ServerNodeType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.Entry;
import net.firejack.platform.core.model.registry.Environments;
import net.firejack.platform.core.model.registry.domain.Environment;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.*;
import net.firejack.platform.core.utils.EnvironmentsUtils;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.security.x509.KeyUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.security.x509.X509CertImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
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

@TrackDetails
@Component
public class RegistrySlaveNodeBroker extends ServiceBroker<ServiceRequest<ServerNodeConfig>, ServiceResponse<SimpleIdentifier<InputStream>>> {
	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private IDomainStore domainStore;
	@Autowired
	private ISystemStore systemStore;
	@Autowired
	private IServerStore serverStore;
	@Autowired
	private IDatabaseStore databaseStore;
	@Autowired
	private IFileStore fileStore;

	@Override
	protected ServiceResponse<SimpleIdentifier<InputStream>> perform(ServiceRequest<ServerNodeConfig> request) throws Exception {
		ServerNodeConfig data = request.getData();

		PackageModel model = packageStore.findByLookup(data.getLookup());
		if (model.getSystem() == null) {
			throw new IllegalStateException("Couldn't find an associated system by the specified package: " + data.getLookup());
		}

		SystemModel system = systemStore.findById(model.getSystem().getId());
		if (system != null) {
			X509CertImpl cert = new X509CertImpl(data.getCert());
			PublicKey publicKey = cert.getPublicKey();
			byte[] encodedPubKey = Base64.encode(publicKey.getEncoded());

			ServerModel server = serverStore.findByLookup(DiffUtils.lookup(system.getLookup(), data.getServerName()));
			if (server == null) {
				server = new ServerModel();
				server.setName(data.getServerName());
				server.setPath(system.getLookup());
				server.setParent(system);
				server.setServerName(data.getHost());
				server.setPort(data.getPort());
				server.setActive(false);
				server.setPublicKey(new String(encodedPubKey));

				serverStore.save(server);
			} else if (!server.getPublicKey().equals(new String(encodedPubKey))) {
				throw new BusinessFunctionException("Incorrect public key");
			}

			List<DatabaseModel> databases = databaseStore.findAllDataSources(model);
			List<ServerModel> servers = serverStore.findAllByParentIdWithFilter(system.getId(), null);
			List<FileStoreModel> filestores = fileStore.findAllByParentIdWithFilter(system.getId(), null);
			String xa = domainStore.findXADomains(model.getLookup());
            if (StringUtils.isNotBlank(xa))
                xa +=";"+ model.getLookup();

			Environment environment = new Environment();
			environment.setSystem(system);
			environment.setDatabases(databases);
			environment.setServers(servers);
			environment.setFilestores(filestores);

			List<Entry> environments;
			if (data.getNodeType().equals(ServerNodeType.GATEWAY)) {
				environments = EnvironmentsUtils.convert(environment, model);
			} else {
				environments = new ArrayList<Entry>();
				environments.add(environment);
			}

            environments.add(new Entry("xa", xa));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
			FileUtils.writeJAXB(new Environments(environments), out, Environments.class, Environment.class);

			byte[] encrypted = KeyUtils.encrypt(publicKey, out.toByteArray());

			return new ServiceResponse(new SimpleIdentifier<InputStream>(new ByteArrayInputStream(encrypted)), "Server Node registered.", true);
		}
		return new ServiceResponse("Failed to find parent system .", false);
	}
}
