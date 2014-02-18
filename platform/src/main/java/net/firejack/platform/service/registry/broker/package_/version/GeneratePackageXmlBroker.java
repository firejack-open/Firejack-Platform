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


package net.firejack.platform.service.registry.broker.package_.version;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.PackageVersion;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.export.IPackageExporter;
import net.firejack.platform.core.config.export.PackageExportResult;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@TrackDetails
@Component("generatePackageXmlBroker")
@ProgressStatus(weight = 5, description = "Generate package xml")
public class GeneratePackageXmlBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<PackageVersion>> {

	@Autowired
	private FileHelper helper;

	@Autowired
	private IPackageStore packageStore;

	@Autowired
    @Qualifier("basicPackageExporter")
	private IPackageExporter packageExporter;

	@Autowired
	private PackageVersionHelper packageVersionHelper;

	@Override
	protected ServiceResponse<PackageVersion> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
		Long packageId = request.getData().getIdentifier();
		ServiceResponse<PackageVersion> response;

		try {
			PackageExportResult exportResult = packageExporter.exportPackage(packageId);

			PackageModel packageRN = packageStore.findById(packageId);

			Map<String, InputStream> filePaths = new HashMap<String, InputStream>();
			String packageFolder = FileUtils.construct(helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));

			String packageFileName = packageRN.getName() + PackageFileType.PACKAGE_XML.getDotExtension();
			byte[] bytes = exportResult.getPackageXml().getBytes();
			OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,packageFileName,new ByteArrayInputStream(bytes),packageFolder);
			filePaths.put(PackageFileType.PACKAGE_XML.getOfrFileName(),  new ByteArrayInputStream(bytes));

			String warFileName = packageRN.getName() + PackageFileType.APP_WAR.getDotExtension();
			InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, warFileName,packageFolder);
			if (stream!=null) {
				filePaths.put(PackageFileType.APP_WAR.getOfrFileName(), stream);
			}

			String generatedResourceFilename = exportResult.getGeneratedResourceFilePath();
			if (StringUtils.isNotBlank(generatedResourceFilename)) {
				String resourceFileName = packageRN.getName() + PackageFileType.RESOURCE_ZIP.getDotExtension();

				InputStream inputStream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, generatedResourceFilename,helper.getTemp());
				OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,resourceFileName,inputStream, packageFolder);
				IOUtils.closeQuietly(inputStream);
				inputStream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, generatedResourceFilename,helper.getTemp());
				filePaths.put(PackageFileType.RESOURCE_ZIP.getOfrFileName(), inputStream);
			}

			String packageFilename = packageRN.getName() + PackageFileType.PACKAGE_OFR.getDotExtension();
			OPFEngine.FileStoreService.zip(OpenFlame.FILESTORE_BASE,filePaths,packageFolder, packageFilename );

			PackageVersion packageVersion = packageVersionHelper.populatePackageVersion(packageRN);
			response = new ServiceResponse<PackageVersion>(packageVersion, "Generate package xml successfully", true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response = new ServiceResponse<PackageVersion>("Generate package xml fail", false);
		}

		return response;
	}
}
