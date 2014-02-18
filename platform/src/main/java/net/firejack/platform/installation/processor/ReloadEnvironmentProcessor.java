package net.firejack.platform.installation.processor;


import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.model.registry.Environments;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.Environment;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.model.registry.system.FileStoreModel;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.installation.processor.event.ReloadEnvironmentEvent;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.web.security.x509.KeyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * User: sergey
 * Date: 1:09 PM 11/7/11
 */
@Component
public class ReloadEnvironmentProcessor implements ApplicationListener<ReloadEnvironmentEvent> {
	private static final Logger logger = Logger.getLogger(ReloadEnvironmentProcessor.class);
	@Autowired
	private IPackageStore packageStore;
	@Resource(name = "registryNodeStories")
	private Map<RegistryNodeType, IRegistryNodeStore<LookupModel>> registryNodeStories;
	@Autowired
	private IUserStore userStore;
	@Autowired
	private SetupSystemProcessor systemProcessor;
	@Autowired
	private OpenFlameDataSource source;

	@Override
	public void onApplicationEvent(ReloadEnvironmentEvent event) {
		try {
			execute();
		} catch (Throwable throwable) {
			logger.error(throwable, throwable);
		}
	}

	@Scheduled(cron = "0 */2 * * * ?")
	public void execute() throws Throwable {
		if (ConfigContainer.isAppInstalled()) {

            String xml = FileUtils.md5(InstallUtils.getXmlEnv());
            String properties = FileUtils.md5(InstallUtils.getPropEnv());

			if (xml != null && !OpenFlameConfig.APP_ENV_XML.getValue().equals(xml)) {
				OpenFlameConfig.APP_ENV_XML.setValue(xml);
				reloadEnvFromXml();
				systemProcessor.setupSystemURL();
			}
			if (properties != null && !OpenFlameConfig.APP_ENV_PROPERTIES.getValue().equals(properties)) {
				OpenFlameConfig.APP_ENV_PROPERTIES.setValue(properties);
				reloadEnvProperties();
			}
		}
	}

	/**
	 * @param props
	 * @param file
	 */
	public void overwriteProperties(Map props, File file) {
		try {
			KeyUtils.setProperties(file, InstallUtils.getKeyStore(), props);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/***/
	public void reloadEnvProperties() {
		logger.info("Reload Env Properties");
		File file = InstallUtils.getPropEnv();
		Map<String, String> newProps = MiscUtils.getProperties(file);
		ConfigContainer.putAll(newProps);

		String name = OpenFlameConfig.APP_ADMIN_NAME.getValue();
		String email = OpenFlameConfig.APP_ADMIN_EMAIL.getValue();
		String password = OpenFlameConfig.APP_ADMIN_PASSWORD.getValue();
		String hashed = SecurityHelper.hash(password);

		UserModel user = userStore.findUserByUsername(name);
		if (user != null && (!user.getUsername().equals(name) || !user.getEmail().equals(email) || !user.getPassword().equals(hashed))) {
			user.setUsername(name);
			user.setEmail(email);
			user.setPassword(hashed);
			userStore.saveOrUpdate(user);
		}

		overwriteProperties(OpenFlameConfig.filter(), Env.getDefaultEnvFile());
	}

	/***/
	public void reloadEnvFromXml() {
		logger.info("Reload Env Xml");
		File file = InstallUtils.getXmlEnv();

		if (file.exists()) {
			Map<String, String> newProps = EnvironmentsUtils.convertFromXml(file);
			ConfigContainer.putAll(newProps);
			source.refreshDBProperties();

			Environments environment = EnvironmentsUtils.deserialize(file);
			iterate(environment);

			overwriteProperties(OpenFlameConfig.filter(), Env.getDefaultEnvFile());
		} else {
			logger.warn("Can't find environment.xml: " + file.getAbsolutePath());
		}
	}

	/**
	 * @param environment
	 */
	public void iterate(Environments<Environment> environment) {
		for (Environment item : environment.getEnvironments()) {
			processSingle(item.getSystem());
			processPack((Collection) item.getDatabases());
			processPack((Collection) item.getServers());
			processPack((Collection) item.getFilestores());
		}
	}

	@SuppressWarnings("unchecked")
	private void processSingle(LookupModel model) {
		if (model == null) {
			return;
		}

		List<PackageModel> packages = null;
		IRegistryNodeStore store = registryNodeStories.get(model.getType());
		String lookup = DiffUtils.lookup(model.getPath(), model.getName());
		LookupModel savedObj = store.findByLookup(lookup, true);
		if (savedObj != null) {
			if (!hasSameData(model, savedObj)) {
				ClassUtils.copyProperties(model, savedObj, false);
				if (savedObj instanceof SystemModel) {
					SystemModel system = (SystemModel) savedObj;
					packages = system.getAssociatedPackages();
					system.setAssociatedPackages(null);
				}
				store.save(savedObj);
				if (packages != null) {
					for (PackageModel packageModel : packages) {
						packageModel = packageStore.findByLookup(packageModel.getLookup());
						packageStore.associate(packageModel, (SystemModel) savedObj);
					}
				}
			}
		}
	}

	private void processPack(Collection<LookupModel> collection) {
		if (collection != null && !collection.isEmpty()) {
			for (LookupModel model : collection) {
				processSingle(model);
			}
		}
	}

	private boolean hasSameData(LookupModel first, LookupModel second) {
		if (first == null || second == null) {
			return false;
		}

		if (first instanceof SystemModel) {
			return ((SystemModel) first).hasIdenticalData(second);
		} else if (first instanceof DatabaseModel) {
			return ((DatabaseModel) first).hasIdenticalData(second);
		} else if (first instanceof ServerModel) {
			return ((ServerModel) first).hasIdenticalData(second);
		} else if (first instanceof FileStoreModel) {
			return ((FileStoreModel) first).hasIdenticalData(second);
		}

		return false;
	}
}
