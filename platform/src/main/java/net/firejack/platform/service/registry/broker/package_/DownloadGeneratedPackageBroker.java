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
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@TrackDetails
@Component("downloadGeneratedPackageBroker")
public class DownloadGeneratedPackageBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<FileInfo>> {
	@Autowired
	private FileHelper helper;

	@Autowired
	private IPackageStore store;

	@Override
	protected ServiceResponse<FileInfo> perform(ServiceRequest<NamedValues> message) throws Exception {
		Long packageId = (Long) message.getData().get("packageId");
		String packageFilename = (String) message.getData().get("packageFilename");

		PackageModel packageRN = store.findById(packageId);
		ServiceResponse<FileInfo> response = new ServiceResponse<FileInfo>();
		InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, packageFilename,helper.getVersion(), String.valueOf(packageId), String.valueOf(packageRN.getVersion()));

		FileInfo fileInfo = new FileInfo();
		fileInfo.setStream(stream);
		fileInfo.setFilename(packageFilename);
		response.addItem(fileInfo);
		response.setSuccess(true);

		return response;
	}
}
