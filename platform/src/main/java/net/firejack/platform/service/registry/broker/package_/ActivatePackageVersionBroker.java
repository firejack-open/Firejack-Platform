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
package net.firejack.platform.service.registry.broker.package_;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.PackageVersion;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.translate.StatusProviderTranslationResult;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@TrackDetails
@ProgressComponent(weight = 20)
@Component("activatePackageVersionBroker")
public class ActivatePackageVersionBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<Package>> {
	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private FileHelper helper;
	@Autowired
	private PackageInstallationService packageInstallationService;
	@Autowired
	private PackageVersionHelper packageVersionHelper;

	@Override
	protected ServiceResponse<Package> perform(ServiceRequest<NamedValues> request) throws Exception {
		Long packageId = (Long) request.getData().get("packageId");
		Integer version = (Integer) request.getData().get("version");

		PackageModel packageRN = packageStore.findById(packageId);

		String packageVersionFolder = FileUtils.construct(helper.getVersion(), packageId.toString(), version.toString());

		InputStream packageXmlFile = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE,packageRN.getName() + PackageFileType.PACKAGE_XML.getDotExtension(),packageVersionFolder);
		InputStream resourceZipFile = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageRN.getName() + PackageFileType.RESOURCE_ZIP.getDotExtension(),packageVersionFolder);

		StatusProviderTranslationResult result = packageInstallationService.activatePackage(packageXmlFile, resourceZipFile);
		IOUtils.closeQuietly(packageXmlFile);
		IOUtils.closeQuietly(resourceZipFile);

		if (result.getResult()) {
			Integer oldVersion = packageRN.getVersion();
			packageRN = packageStore.updatePackageVersion(packageId, version);
			try {
				packageVersionHelper.archiveVersion(packageRN, oldVersion, result.getOldPackageXml());
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

		Package packageVO = factory.convertTo(Package.class, packageRN);
		PackageVersion packageVersionVO = packageVersionHelper.populatePackageVersion(packageRN);
		packageVO.setPackageVersion(packageVersionVO);
		return new ServiceResponse<Package>(packageVO, "activate package version successfully", true);
	}
}
