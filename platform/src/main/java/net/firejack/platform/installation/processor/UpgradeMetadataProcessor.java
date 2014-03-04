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
