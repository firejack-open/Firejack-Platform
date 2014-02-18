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
import net.firejack.platform.core.config.installer.RequestTaskService;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.generate.service.IResourceGeneratorService;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@TrackDetails
@Component("uploadPackageVersionFileBroker")
public class UploadPackageVersionFileBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<PackageVersion>> {
	@Autowired
	private FileHelper helper;
	@Autowired
	private IPackageStore store;
	@Autowired
	private IResourceGeneratorService resourceGeneratorService;
	@Autowired
	private PackageVersionHelper packageVersionHelper;
	@Autowired
	private RequestTaskService requestTaskService;

	@Override
	protected ServiceResponse<PackageVersion> perform(ServiceRequest<NamedValues> request) throws Exception {
		String type = (String) request.getData().get("fileType");
		Long packageId = (Long) request.getData().get("packageId");
		InputStream inputStream = (InputStream) request.getData().get("inputStream");

		PackageModel packageRN = store.findById(packageId);
		if (packageRN == null) {
			throw new BusinessFunctionException(null, "Could not found package by id:[" + packageId + "]");
		}
		PackageFileType fileType = PackageFileType.findByExtension(type);
		if (fileType == null || !PackageFileType.APP_WAR.equals(fileType)) {
			throw new BusinessFunctionException(null, "Should be uploaded only application war file.");
		}

		PackageVersion packageVersionVO;
		try {
			String fileName = packageRN.getName() + fileType.getDotExtension();
			OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_BASE,fileName,inputStream,helper.getVersion(), String.valueOf(packageRN.getId()), String.valueOf(packageRN.getVersion()));
			resourceGeneratorService.generateOFR(packageRN);
			packageVersionVO = packageVersionHelper.populatePackageVersion(packageRN);
		} catch (IOException e) {
			throw new BusinessFunctionException();
		}

		if (fileType == PackageFileType.APP_WAR) {
            String name = packageRN.getUrlPath();
            if (packageRN.getUrlPath() == null || packageRN.getUrlPath().isEmpty()) name = "ROOT";
            name = name + PackageFileType.APP_WAR.getDotExtension();
			String file = packageRN.getName() + PackageFileType.APP_WAR.getDotExtension();

			requestTaskService.undeploy(packageRN.getId(), name);
			requestTaskService.deploy(packageRN.getId(), name, file);
		}
		return new ServiceResponse<PackageVersion>(packageVersionVO, "File has uploaded successfully.", true);
	}
}
