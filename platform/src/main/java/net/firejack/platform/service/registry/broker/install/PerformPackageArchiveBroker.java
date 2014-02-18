package net.firejack.platform.service.registry.broker.install;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.export.IPackageExporter;
import net.firejack.platform.core.config.export.PackageExportResult;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.translate.StatusProviderTranslationResult;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.VersionUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.mina.annotations.ProgressComponent;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

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
@Component("performPackageArchiveBroker")
@ProgressComponent(weight = 40)
public class PerformPackageArchiveBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {

	@Autowired
	private FileHelper helper;

	@Autowired
	private IPackageStore packageStore;

	@Autowired
	@Qualifier("basicPackageExporter")
	private IPackageExporter packageExporter;

	@Autowired
	private PackageVersionHelper packageVersionHelper;

	@Autowired
	private PackageInstallationService packageInstallationService;


	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
		String uploadedFilename = (String) request.getData().get("uploadedFilename");
		String versionName = (String) request.getData().get("versionName");
		Boolean doAsCurrent = (Boolean) request.getData().get("doAsCurrent");
		Boolean doArchive = (Boolean) request.getData().get("doArchive");

		Integer version = VersionUtils.convertToNumber(versionName);

		InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, PackageFileType.PACKAGE_XML.getOfrFileName() + "." + uploadedFilename, helper.getTemp());
		PackageModel packageRN;
		IPackageDescriptor patchProcessor;
		if (StringUtils.isBlank(uploadedFilename) || stream == null) {
			throw new BusinessFunctionException(null, "file.is.not.exist", PackageFileType.PACKAGE_XML.name());
		} else {
			patchProcessor = packageInstallationService.getPackageDescriptor(stream);
			IOUtils.closeQuietly(stream);
			String packageLookup = patchProcessor.getPath() + "." + StringUtils.normalize(patchProcessor.getName());
			packageRN = packageStore.findByLookup(packageLookup);
			if (packageRN == null) {
				throw new BusinessFunctionException(null, "package.not.found.by.lookup", packageLookup);
			}
		}

		try {
			InputStream resourceStream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, PackageFileType.RESOURCE_ZIP.getOfrFileName() + "." + uploadedFilename,helper.getTemp());
			InputStream warStream= OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, PackageFileType.APP_WAR.getOfrFileName() + "." + uploadedFilename,helper.getTemp());

			String packageXmlFile = packageRN.getName() + PackageFileType.PACKAGE_XML.getDotExtension();

			if (doArchive) {
				IPackageDescriptor descriptor = packageExporter.exportPackageDescriptor(packageRN.getId());
				PackageExportResult result = packageExporter.exportPackageDescriptor(descriptor);
				packageVersionHelper.archiveVersion(packageRN, packageRN.getVersion(), result.getPackageXml());
			}

			Integer packageXmlVersion = VersionUtils.convertToNumber(patchProcessor.getVersion());
			if (!packageXmlVersion.equals(version)) {
				stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, PackageFileType.PACKAGE_XML.getOfrFileName() + "." + uploadedFilename, helper.getTemp());
				InputStream inputStream = packageVersionHelper.changeVersionNumber(stream, version);
				OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, PackageFileType.PACKAGE_XML.name() + uploadedFilename, inputStream, helper.getTemp());
				IOUtils.closeQuietly(stream);
				IOUtils.closeQuietly(inputStream);
			}

			stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, PackageFileType.PACKAGE_XML.getOfrFileName() + "." + uploadedFilename,helper.getTemp());
			OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE, packageXmlFile, stream, helper.getVersion(), String.valueOf(packageRN.getId()), version.toString());
			IOUtils.closeQuietly(stream);


			String  resourceZipFile = packageRN.getName() + PackageFileType.RESOURCE_ZIP.getDotExtension();
			if (resourceStream != null) {
				OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,resourceZipFile,resourceStream, helper.getVersion(), String.valueOf(packageRN.getId()), version.toString());
				IOUtils.closeQuietly(resourceStream);
			}

			if (warStream != null) {
				String war = packageRN.getName() + PackageFileType.APP_WAR.getDotExtension();
				OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,war,warStream, helper.getVersion(), String.valueOf(packageRN.getId()), version.toString());
				IOUtils.closeQuietly(warStream);
			}

			if (doAsCurrent) {
				InputStream packageXml = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageXmlFile,helper.getVersion(), String.valueOf(packageRN.getId()), version.toString());
				InputStream resourceZip = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, resourceZipFile,helper.getVersion(), String.valueOf(packageRN.getId()), version.toString());
				StatusProviderTranslationResult result = packageInstallationService.activatePackage(packageXml, resourceZip);
				IOUtils.closeQuietly(packageXml);
				IOUtils.closeQuietly(resourceZip);
				if (result.getResult()) {
					packageRN = packageStore.updatePackageVersion(packageRN.getId(), version);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return new ServiceResponse("Activate successfully", true);
	}
}
