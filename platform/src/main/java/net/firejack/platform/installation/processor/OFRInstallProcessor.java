/**
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.installation.processor;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.translate.StatusProviderTranslationResult;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.Environments;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.config.ConfigModel;
import net.firejack.platform.core.model.registry.domain.Environment;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.resource.FolderModel;
import net.firejack.platform.core.store.registry.IConfigStore;
import net.firejack.platform.core.store.registry.IEnvironmentStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.installation.scheduler.Command;
import net.firejack.platform.installation.scheduler.CommandListener;
import net.firejack.platform.installation.scheduler.CommandScheduler;
import net.firejack.platform.model.event.CompleteInitEvent;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


@SuppressWarnings("unused")
@Component("OFRInstallProcessor")
public class OFRInstallProcessor implements FilenameFilter, ApplicationListener<CompleteInitEvent>, CommandListener {
	private static final Logger logger = Logger.getLogger(OFRInstallProcessor.class);
	private static final String OFR = "ofr";
	private static final String FILE_NAME = "upgdade";

	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	@Autowired
	@Qualifier("rootDomainStore")
	private IRegistryNodeStore<RootDomainModel> rootDomainIStore;

	@Autowired
	@Qualifier("packageStore")
	private IPackageStore packageStore;

	@Autowired
	@Qualifier("folderStore")
	private IRegistryNodeStore<FolderModel> folderStore;

	@Autowired
	@Qualifier("environmentStore")
	private IEnvironmentStore environmentStore;

	@Autowired
	@Qualifier("configStore")
	private IConfigStore configStore;

	@Autowired
	private FileHelper helper;

	@Autowired
	private PackageInstallationService packageInstallationService;

	@Autowired
	private CommandScheduler scheduler;

	@Autowired
	private PackageVersionHelper packageVersionHelper;

	private XPathFactory factory = XPathFactory.newInstance();

	@Override
	public void onApplicationEvent(CompleteInitEvent event) {
		try {
			scanning();
			scheduler.addListener(Command.upgrade, this);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void execute() throws Exception {
		scanning();
	}

	/**
	 * @throws Exception
	 */
	public synchronized void scanning() throws Exception {
		Collection<Module> modules = searchInstallOFR();
		install(modules);
		installConfigs();
		installSystem();
	}

	private void install(Collection<Module> modules) throws IOException {
		if (modules != null) {
			for (Module module : modules) {
				if (!module.isExist()) {
					installOFR(module.getOfr());
				}
				install(module.getChildren());
			}
		}
	}

	private void installOFR(File ofr) throws IOException {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(ofr);

			String packageXmlUploadedFile = null;
			String resourceZipUploadedFile = null;
			String codeWarUploadedFile = null;
			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();

				if (PackageFileType.PACKAGE_XML.getOfrFileName().equals(entry.getName())) {
					packageXmlUploadedFile = SecurityHelper.generateRandomSequence(16) + PackageFileType.PACKAGE_XML.getDotExtension();
					OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,packageXmlUploadedFile,zipFile.getInputStream(entry), helper.getTemp());
				} else if (PackageFileType.RESOURCE_ZIP.getOfrFileName().equals(entry.getName())) {
					resourceZipUploadedFile = SecurityHelper.generateRandomSequence(16) + PackageFileType.RESOURCE_ZIP.getDotExtension();
					OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, resourceZipUploadedFile,zipFile.getInputStream(entry), helper.getTemp());
				} else if (PackageFileType.APP_WAR.getOfrFileName().equals(entry.getName())) {
					codeWarUploadedFile = SecurityHelper.generateRandomSequence(16) + PackageFileType.APP_WAR.getDotExtension();
					OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, codeWarUploadedFile,zipFile.getInputStream(entry), helper.getTemp());
				}
			}

			InputStream packageXml = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageXmlUploadedFile,helper.getTemp());
			InputStream resourceZip = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, resourceZipUploadedFile,helper.getTemp());
			StatusProviderTranslationResult statusResult = packageInstallationService.activatePackage(packageXml, resourceZip);
			IOUtils.closeQuietly(packageXml);
			IOUtils.closeQuietly(resourceZip);

			if (statusResult.getResult() && statusResult.getPackage() != null) {
				PackageModel packageRN = statusResult.getPackage();
				if (packageRN != null) {
					Integer oldVersion = packageRN.getVersion();
					packageRN = packageStore.updatePackageVersion(packageRN.getId(), statusResult.getVersionNumber());

					if (statusResult.getOldPackageXml() != null) {
						packageVersionHelper.archiveVersion(packageRN, oldVersion, statusResult.getOldPackageXml());
					}

					String packageXmlName = packageRN.getName() + PackageFileType.PACKAGE_XML.getDotExtension();
					packageXml = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageXmlUploadedFile,helper.getTemp());
					OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, packageXmlName,packageXml, helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));
					IOUtils.closeQuietly(packageXml);

					if (resourceZipUploadedFile != null) {
						String resourceZipName = packageRN.getName() + PackageFileType.RESOURCE_ZIP.getDotExtension();
						resourceZip = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, resourceZipUploadedFile,helper.getTemp());
						OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, resourceZipName,resourceZip, helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));
						IOUtils.closeQuietly(resourceZip);
					}

//                    File webapps = new File(Env.CATALINA_BASE.getValue(), "webapps");
//                    if (codeWarUploadedFile != null && webapps.exists()) {
//                        FileUtils.copyFile(codeWarUploadedFile, new File(webapps, packageRN.getName() + PackageFileType.APP_WAR.getDotExtension()));
//                    }

					if (codeWarUploadedFile != null) {
						String codeWarName = packageRN.getName() + PackageFileType.APP_WAR.getDotExtension();
						InputStream warstream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, codeWarUploadedFile,helper.getTemp());
						OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, codeWarName,warstream, helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));
						IOUtils.closeQuietly(warstream);
					}

					String packageFilename = packageRN.getName() + PackageFileType.PACKAGE_OFR.getDotExtension();
					FileInputStream stream = new FileInputStream(ofr);
					OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, packageFilename,stream, helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));
					IOUtils.closeQuietly(stream);
				} else {
					throw new BusinessFunctionException("Package archive has not created.");
				}
			}
		} finally {
			if (zipFile != null) zipFile.close();
		}
	}

	private void installConfigs() throws IOException {
		Map<String, String> configs = ConfigContainer.getOpfp();
		if (configs.isEmpty()) {
			return;
		}

		List<ConfigModel> exists = configStore.findAllByLookupList(configs.keySet());

		for (ConfigModel config : exists) {
			String lookup = config.getLookup();
			String value = configs.remove(lookup);
			if (!config.getValue().equals(value)) {
				config.setValue(value);
				configStore.save(config);
			}
		}

		for (Map.Entry<String, String> entry : configs.entrySet()) {
			String lookup = entry.getKey();
			String[] names = splitLookup(lookup);
			int index = 0;

			RegistryNodeModel node = find(names, names.length - 1);
			if (node != null) {
				lookup = lookup.replace(node.getLookup(), "");
				names = splitLookup(lookup);
				index = 1;
			}

			createConfig(node, names, index, entry.getValue());
		}
	}

	/***/
	public void installSystem() {
		File xml = InstallUtils.getXmlEnv();
		if (xml.exists()) {
			Environments deserialize = EnvironmentsUtils.deserialize(xml);
			List<Environment> environments = deserialize.getEnvironments();
			for (Environment environment : environments) {
				SystemModel system = environment.getSystem();
				String lookup = DiffUtils.lookup(system.getPath(), system.getName());
				RegistryNodeModel sys = registryNodeStore.findByLookup(lookup);
				if (sys == null) {
					RootDomainModel domain = (RootDomainModel) registryNodeStore.findByLookup(system.getPath());
					if (domain != null) {
						environmentStore.save(domain, environment);
					}
				}
			}
		}
	}

	private void createConfig(RegistryNodeModel parent, String[] names, int index, String value) {
		if (index < names.length) {
			if (index == (names.length - 1)) {
				ConfigModel config = new ConfigModel();
				config.setParent(parent);
				config.setPath(parent.getLookup());
				config.setLookup(DiffUtils.lookup(parent.getLookup(), names[index]));
				config.setName(names[index]);
				config.setValue(value);

				configStore.save(config);
			} else if (index == 0) {
				RootDomainModel domain = new RootDomainModel();
				domain.setName(names[1] + "." + names[0]);
				domain.setLookup(names[0] + "." + names[1]);

				rootDomainIStore.save(domain);

				index = 1;
				parent = domain;
			} else {
				FolderModel folder = new FolderModel();
				folder.setParent(parent);
				folder.setName(names[index]);

				folderStore.save(folder);
				parent = folder;
			}

			createConfig(parent, names, ++index, value);
		}
	}

	private RegistryNodeModel find(String[] names, int count) {
		if (count == 1) return null;

		String lookup = buildLookup(names, count);
		RegistryNodeModel node = registryNodeStore.findByLookup(lookup);
		if (node == null) {
			return find(names, --count);
		}
		return node;
	}

	private String buildLookup(String[] names, int count) {
		String result = "";
		for (int i = 0; i < count; i++) {
			if (i == 0) {
				result += names[i];
			} else {
				result += "." + names[i];
			}
		}
		return result;
	}

	private String[] splitLookup(String lookup) {
		return lookup.split("\\.");
	}

	private Module getModule(File ofr) throws IOException, XPathExpressionException {
		XPath xpath = factory.newXPath();
		InputStream file = ArchiveUtils.getFile(ofr, PackageFileType.PACKAGE_XML.getOfrFileName());
		Object evaluate = xpath.evaluate("/package", new InputSource(file), XPathConstants.NODE);
		String path = (String) xpath.evaluate("@path", evaluate, XPathConstants.STRING);
		String name = (String) xpath.evaluate("@name", evaluate, XPathConstants.STRING);
		String version = (String) xpath.evaluate("@version", evaluate, XPathConstants.STRING);
		String dependencies = (String) xpath.evaluate("@dependencies", evaluate, XPathConstants.STRING);
		IOUtils.closeQuietly(file);

		String lookup = path + "." + name;
		Integer ver = VersionUtils.convertToNumber(version);

		return new Module(ofr, lookup, ver, dependencies);
	}

	private Collection<Module> searchInstallOFR() throws IOException, XPathExpressionException {
		Map<String, Module> result = new HashMap<String, Module>();

		File[] ofrs = Env.getEnvFile(OFR).listFiles(this);
		if (ofrs != null) {
			for (File ofr : ofrs) {
				Module module = getModule(ofr);
				result.put(module.getLookup(), module);
			}

			for (Module module : result.values()) {
				PackageModel pkg = packageStore.findByLookup(module.getLookup());
				if (pkg != null) {
					module.setExist(pkg.getVersion().equals(module.getVersion()));
				}
				Module dep = result.get(module.getDependencies());
				if (dep != null) {
					dep.addChild(module);
				}
			}

			for (Iterator<Map.Entry<String, Module>> iterator = result.entrySet().iterator(); iterator.hasNext(); ) {
				Map.Entry<String, Module> entry = iterator.next();
				if (!entry.getValue().isRoot()) {
					iterator.remove();
				}
			}
		}
		return result.values();
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(".ofr");
	}


	private class Module extends Tree<Module> {
		private File ofr;
		private String lookup;
		private Integer version;
		private String dependencies;
		private boolean exist;

		private Module(File ofr, String lookup, Integer version, String dependencies) {
			this.ofr = ofr;
			this.lookup = lookup;
			this.version = version;
			this.dependencies = dependencies;
		}

		public File getOfr() {
			return ofr;
		}

		public String getLookup() {
			return lookup;
		}

		public void setLookup(String lookup) {
			this.lookup = lookup;
		}

		public String getDependencies() {
			return dependencies;
		}

		public boolean isExist() {
			return exist;
		}

		public void setExist(boolean exist) {
			this.exist = exist;
		}

		public Integer getVersion() {
			return version;
		}
	}
}
