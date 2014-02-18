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

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.config.translate.StatusProviderTranslationResult;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.installation.processor.event.OpenflameUpgradeEvent;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


@SuppressWarnings("unused")
@Component
public class UpgradeMetadataProcessor implements ApplicationListener<OpenflameUpgradeEvent> {

	private static final Logger logger = Logger.getLogger(UpgradeMetadataProcessor.class);

	@Autowired
	@Qualifier("packageInstallationService")
	private PackageInstallationService packageInstallationService;

	@Autowired
	private FileHelper helper;

	@Autowired
	private IPackageStore packageStore;

	@Autowired
	private PackageVersionHelper packageVersionHelper;

	@Override
	public void onApplicationEvent(OpenflameUpgradeEvent event) {
		try {
			upgradeMetadata();
		} catch (OpenFlameRuntimeException e) {
			logger.error(e.getMessage());
		}
	}

	/***/
	public void upgradeMetadata() {
		PackageModel packageRN = packageStore.findByLookup(OpenFlame.PACKAGE);
		if (packageRN != null) {
			if (packageRN.getDatabaseVersion() > packageRN.getVersion()) {
				File packageXmlInstallationFile = FileUtils.getResource("dbupdate", PackageFileType.PACKAGE_XML.getOfrFileName());
				File resourceZipInstallationFile = FileUtils.getResource("dbupdate", PackageFileType.RESOURCE_ZIP.getOfrFileName());

				try {
					FileInputStream packageXml = new FileInputStream(packageXmlInstallationFile);
					FileInputStream resourceZip = new FileInputStream(resourceZipInstallationFile);

					StatusProviderTranslationResult result = packageInstallationService.activatePackage(packageXml, resourceZip);

					IOUtils.closeQuietly(packageXml);
					IOUtils.closeQuietly(resourceZip);
					if (result.getResult()) {
						Integer oldVersion = packageRN.getVersion();
						packageRN = packageStore.updatePackageVersion(packageRN.getId(), result.getVersionNumber());
						try {
							packageVersionHelper.archiveVersion(packageRN, oldVersion, result.getOldPackageXml());

							packageXml = new FileInputStream(packageXmlInstallationFile);
							String packageXmlName = packageRN.getName() + PackageFileType.PACKAGE_XML.getDotExtension();
							OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, packageXmlName,packageXml, helper.getVersion(), String.valueOf(packageRN.getId()), packageRN.getVersion().toString());
							IOUtils.closeQuietly(packageXml);

							resourceZip = new FileInputStream(packageXmlInstallationFile);
							String resourceZipName = packageRN.getName() + PackageFileType.RESOURCE_ZIP.getDotExtension();
							OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,resourceZipName,resourceZip, helper.getVersion(), String.valueOf(packageRN.getId()), packageRN.getVersion().toString());
							IOUtils.closeQuietly(resourceZip);
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
					}
				} catch (FileNotFoundException e) {
					logger.error(e);
				}
			}
		} else {
			logger.error("Could not find OpenFlame package");
		}
	}

}
