package net.firejack.platform.installation.processor;
/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
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


import net.firejack.platform.api.registry.domain.Database;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.config.installer.IDatabaseManager;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.translate.StatusProviderTranslationResult;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.RootDomainModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.model.user.UserModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.registry.*;
import net.firejack.platform.core.store.user.IUserStore;
import net.firejack.platform.core.utils.Factory;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.installation.processor.event.BuildTemplateEvent;
import net.firejack.platform.installation.processor.event.PrepareTemplateEvent;
import net.firejack.platform.installation.processor.event.TemplateEvent;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.service.deployment.broker.DeployBroker;
import net.firejack.platform.service.registry.broker.package_.GenerateWarBroker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TemplateApplicationProcessor implements ApplicationListener<TemplateEvent>{
	private static final Logger logger = Logger.getLogger(TemplateApplicationProcessor.class);
	@Autowired
	private PackageInstallationService packageInstallationService;
	@Autowired
	private IRootDomainStore rootDomainStore;
	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private ISystemStore systemStore;
	@Autowired
	private IDatabaseStore databaseStore;
	@Autowired
	private IRoleStore roleStore;
	@Autowired
	private IUserStore userStore;
	@Autowired
	private GenerateWarBroker generateWarBroker;
    @Autowired
    private IDatabaseManager databaseManager;
    @Autowired
	private DeployBroker deployBroker;
    @Autowired
    private Factory factory;
	@Value("${generated.app}")
	private boolean enabled;

	private ThreadLocal<PackageModel> packageModel = new ThreadLocal<PackageModel>();

	@Override
	public void onApplicationEvent(TemplateEvent event) {
		if (!enabled) return;
		if (event instanceof PrepareTemplateEvent) {
			File packageXml = FileUtils.getResource("dbupdate", "gateway", "package.xml");
			File resourceZip = FileUtils.getResource("dbupdate", "gateway", "resource.zip");

			if (packageXml.exists()) {
				StatusProviderTranslationResult translate = null;
				try {
					InputStream packageStream = FileUtils.openInputStream(packageXml);
					if (event.isModify()) {
						packageStream = modify(packageStream, event);
					}
					InputStream resourceStream = FileUtils.openInputStream(resourceZip);

					translate = packageInstallationService.activatePackage(packageStream, resourceStream);

					IOUtils.closeQuietly(packageStream);
					IOUtils.closeQuietly(resourceStream);
				} catch (IOException e) {
					logger.error(e, e);
				}

				if (translate != null) {
					PackageModel packageModel = translate.getPackage();

					SystemModel systemModel = systemStore.findByLookup(OpenFlame.SYSTEM);
					String serverName = systemModel.getServerName();
					String[] domainUrlParts = serverName.split("\\.");

					if (!serverName.equals("localhost") && domainUrlParts.length != 1 &&
							!serverName.matches("^(\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b)$")) {
						ArrayUtils.reverse(domainUrlParts);

						String rootDomainLookup = domainUrlParts[0] + "." + domainUrlParts[1];
						RootDomainModel rootDomain = rootDomainStore.findByLookup(rootDomainLookup);

						packageModel.setParent(rootDomain);
					}

                    DatabaseModel database = databaseStore.findByLookup(OpenFlame.DATABASE);
                    database.setId(null);
                    database.setName("gateway");
                    database.setUrlPath("gateway");
                    database.setHash(null);
                    database.setUid(null);
                    databaseStore.save(database);

                    packageModel.setDatabase(database);
                    packageStore.associate(packageModel, systemModel);

					RoleModel adminRole = roleStore.findByLookup(OpenFlame.ROLE_ADMIN);
					List<UserModel> users = userStore.findByRole(adminRole.getId());

					String admin = DiffUtils.lookup(packageModel.getLookup(), "admin");
					RoleModel role = roleStore.findByLookup(admin);
					if (role != null) {
						for (UserModel user : users) {
							UserRoleModel userRoleModel = new UserRoleModel(user, role);
							user.getUserRoles().add(userRoleModel);
							userStore.saveOrUpdate(user);
						}
					}

					this.packageModel.set(packageModel);
				}
			}
		} else if (event instanceof BuildTemplateEvent && packageModel.get() != null) {
			PackageModel packageModel = this.packageModel.get();
			generateWarBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(packageModel.getId())));

			String name = packageModel.getUrlPath();
			if (packageModel.getUrlPath() == null || packageModel.getUrlPath().isEmpty()) name = "ROOT";
			String war = name + PackageFileType.APP_WAR.getDotExtension();
			String file = packageModel.getName() + PackageFileType.APP_WAR.getDotExtension();


            Database database = factory.convertTo(Database.class, packageModel.getDatabase());
            databaseManager.createDatabase(database);

			NamedValues values = new NamedValues();
			values.put("packageId", packageModel.getId());
			values.put("name", war);
			values.put("file", file);
			deployBroker.execute(new ServiceRequest<NamedValues>(values));
			this.packageModel.remove();
		}
	}

	public InputStream modify(InputStream stream, TemplateEvent event) throws IOException {
		String xml = IOUtils.toString(stream);
		IOUtils.closeQuietly(stream);

		Pattern pattern = Pattern.compile("package\\s+path\\s*=\\s*\"(.*?)\"\\s+name\\s*=\\s*\"(.*?)\"");

		Matcher matcher = pattern.matcher(xml);
		StringBuffer output = new StringBuffer();
		while (matcher.find()) {
			event.setLookup(DiffUtils.lookup(matcher.group(1), matcher.group(2)));
			matcher.appendReplacement(output, "package path=\"" + event.getPath() + "\" name=\"" + event.getName() + "\"");
		}
		matcher.appendTail(output);

		pattern = Pattern.compile("uid\\s*=\\s*\"(.*?)\"");
		matcher = pattern.matcher(output);

		output = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(output, "uid=\"" + SecurityHelper.generateSecureId() + "\"");
		}
		matcher.appendTail(output);

		xml = output.toString().replaceAll(event.getLookup(), DiffUtils.lookup(event.getPath(), event.getName()));
		return IOUtils.toInputStream(xml);
	}
}
