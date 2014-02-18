/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.installation.processor;

import com.sun.jersey.core.util.Base64;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.config.content.ImportContentProcessor;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.Environments;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.directory.DirectoryModel;
import net.firejack.platform.core.model.registry.domain.*;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.model.user.SystemUserModel;
import net.firejack.platform.core.store.registry.*;
import net.firejack.platform.core.store.user.ISystemUserStore;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.x509.KeyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class SetupSystemProcessor {
    private static final Logger logger = Logger.getLogger(SetupSystemProcessor.class);

	@Autowired
	private EnvironmentStore environmentStore;

	@Autowired
	@Qualifier("rootDomainStore")
	private IRootDomainStore rootDomainStore;

	@Autowired
	@Qualifier("domainStore")
	private IRegistryNodeStore<DomainModel> domainStore;

	@Autowired
	@Qualifier("systemStore")
	private ISystemStore systemStore;

	@Autowired
	private IServerStore serverStore;
	@Autowired
	private IDatabaseStore databaseStore;

	@Autowired
	@Qualifier("fileStore")
	private IFileStore fileStore;

	@Autowired
	@Qualifier("packageStore")
	private IPackageStore packageStore;

	@Autowired
	@Qualifier("directoryStore")
	private IDirectoryStore directoryStore;

	@Autowired
	@Qualifier("importContentProcessor")
	private ImportContentProcessor importContentProcessor;

	@Autowired
	@Qualifier("systemUserStore")
	private ISystemUserStore systemUserStore;

	@Autowired
	@Qualifier("roleStore")
	private IRoleStore roleStore;
	@Autowired
	private FileHelper helper;


	/***/
	public void setupSystem() {
		String domainUrl = OpenFlameConfig.DOMAIN_URL.getValue();
		Integer port = Integer.parseInt(OpenFlameConfig.PORT.getValue());
		String contextUrl = OpenFlameConfig.CONTEXT_URL.getValue();

		if (domainUrl != null && port != null && contextUrl != null) {
			Env.FIREJACK_URL.setValue(WebUtils.getNormalizedUrl(domainUrl, port, contextUrl));
		}

		RootDomainModel rootDomain = rootDomainStore.findByLookup(OpenFlame.ROOT_DOMAIN);
		if (rootDomain == null) {
			rootDomain = new RootDomainModel();
			rootDomain.setName(DiffUtils.findRootDomainName(OpenFlame.ROOT_DOMAIN));
			rootDomain.setLookup(OpenFlame.ROOT_DOMAIN);
			rootDomainStore.save(rootDomain);
		}

		try {
			Environments<Environment> environments = EnvironmentsUtils.deserialize(InstallUtils.getXmlEnv());
			if (environments != null && !environments.isEmpty()) {
				for (Environment environment : environments.getEnvironments()) {
					if (rootDomain.getLookup().equals(environment.getSystem().getPath())) {
						environmentStore.save(rootDomain, environment);
					}
				}
			}

			loadRepositories();

			File contentXmlInstallationFile = FileUtils.getResource("dbupdate", PackageFileType.CONTENT_XML.getOfrFileName());
//        File resourceZipInstallationFile = FileUtils.getResource("dbupdate", PackageFileType.RESOURCE_ZIP.getOfrFileName());

			FileInputStream stream = new FileInputStream(contentXmlInstallationFile);
			importContentProcessor.importContent(stream, null);
			IOUtils.closeQuietly(stream);

			if (environments != null && !environments.isEmpty()) {
				for (Environment environment : environments.getEnvironments()) {
					if (rootDomain.getLookup().equals(environment.getSystem().getPath())) {
						for (ServerModel model : environment.getServers()) {
							if(model.getName().equals("server")) {
								String name = InetAddress.getLocalHost().getHostName();

								KeyPair pair = KeyUtils.generate(EnvironmentsUtils.getKeyStore());
								byte[] encodedPubKey = Base64.encode(pair.getPublic().getEncoded());

								model.setName(name);
								model.setPublicKey(new String(encodedPubKey));

								serverStore.merge(model);

								KeyUtils.add(EnvironmentsUtils.getKeyStore(), pair, "");
							}
						}
					}
				}
			}

		} catch (Exception e) {
            logger.error(e,e);
			throw new BusinessFunctionException("Occurred error in initialize content.", e);
		}
	}

	public void setupSystemAlias() {
		String domainUrl = OpenFlameConfig.DOMAIN_URL.getValue();
		Integer port = Integer.parseInt(OpenFlameConfig.PORT.getValue());

		String[] domainUrlParts = domainUrl.split("\\.");
		ArrayUtils.reverse(domainUrlParts);
		if (domainUrlParts.length > 1 && !StringUtils.isNumeric(domainUrlParts[0])) {
			if (domainUrlParts.length == 2) {
				domainUrlParts = (String[]) ArrayUtils.add(domainUrlParts, "www");
			}

			String rootDomainLookup = domainUrlParts[0] + "." + domainUrlParts[1];

            RootDomainModel rootDomain = rootDomainStore.findByLookup(rootDomainLookup);
            if (rootDomain == null) {
                rootDomain = new RootDomainModel();
                rootDomain.setName(DiffUtils.findRootDomainName(rootDomainLookup));
                rootDomain.setLookup(rootDomainLookup);
                rootDomainStore.save(rootDomain);
            }

            RegistryNodeModel parent = rootDomain;
            if (domainUrlParts.length > 3) {
                String domainLookup = rootDomainLookup;
                for (int i = 2; i < domainUrlParts.length - 1; i++) {
                    String domainName = domainUrlParts[i];
                    domainLookup += "." + domainName;
                    DomainModel domain = domainStore.findByLookup(domainLookup);
                    if (domain == null) {
                        domain = new DomainModel();
                        domain.setName(domainName);
                        domain.setParent(parent);
                        domainStore.save(domain);
                        parent = domain;
                    }
                }
            }

            String systemName = domainUrlParts[domainUrlParts.length - 1];
            String systemLookup = StringUtils.join(domainUrlParts, ".");
            SystemModel system = systemStore.findByLookup(systemLookup);
            if (system == null) {
                SystemModel mainSystem = systemStore.findByLookup(OpenFlame.SYSTEM);

                system = new SystemModel();
                system.setName(systemName);
                system.setServerName(domainUrl);
                system.setPort(port);
                system.setProtocol(EntityProtocol.HTTP);
                system.setStatus(RegistryNodeStatus.ACTIVE);
                system.setParent(parent);
                system.setMain(mainSystem);
                systemStore.save(system);
            }
		}
	}

	public void associatePackage() {
		SystemModel system = systemStore.findByLookup(OpenFlame.SYSTEM);
		DatabaseModel database = databaseStore.findByLookup(OpenFlame.DATABASE);

		PackageModel openflamePackage = packageStore.findByLookup(OpenFlame.PACKAGE);
		if (openflamePackage != null) {
			openflamePackage.setDatabase(database);
			packageStore.associate(openflamePackage, system);
		} else {
			throw new BusinessFunctionException("Occurred error in package association to system process.");
		}
	}

	public void setupSystemAccount() {
		SystemModel system = systemStore.findByLookup(OpenFlame.SYSTEM);
		SystemUserModel systemUserModel = new SystemUserModel();
		systemUserModel.setSystem(system);
		systemUserModel.setUsername(system.getName());
		systemUserModel.setEmail("admin@admin.net");
		systemUserModel.setConsumerKey(system.getServerName());
		systemUserModel.setConsumerSecret(SecurityHelper.generateRandomSequence(32));
		DirectoryModel accountsDirectory = directoryStore.findByLookup(OpenFlame.SIGN_UP_DIRECTORY);
		systemUserModel.setRegistryNode(accountsDirectory);
		RoleModel systemRole = roleStore.findByLookup(OpenFlame.ROLE_SYSTEM);
		UserRoleModel userRoleModel = new UserRoleModel(null, systemRole);
		systemUserModel.setUserRoles(Arrays.asList(userRoleModel));
		systemUserStore.save(systemUserModel);
	}

	public void loadRepositories() {
		List<FileStoreModel> models = fileStore.findAll();
		for (FileStoreModel model : models) {
			FileStore store = OpenFlameSpringContext.getBean("fileStore");
			store.setBase(model.getServerDirectory());
			OpenFlameSpringContext.addSingleton(model.getLookup(), store);
			if (model.getLookup().equals(OpenFlame.FILESTORE_BASE)) {
				helper.init(model.getServerDirectory());
			}
		}
	}

	/***/
	public void setupSystemURL() {
		PackageModel aPackage = packageStore.findByLookup(OpenFlame.PACKAGE);
		if (aPackage != null) {
			Map<String, String> map = ClassUtils.transformPlaceHolderEntity(aPackage);
			ConfigContainer.putAll(map);
			Env.FIREJACK_URL.setValue(WebUtils.getNormalizedUrl(map.get("package.host"), map.get("package.port"), map.get("context.url")));
		}
	}
}
