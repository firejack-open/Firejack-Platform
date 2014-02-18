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
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.translate.ITranslationResult;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.model.service.PackageInstallationService;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@TrackDetails
@Component("supportPackageVersionBroker")
public class SupportPackageVersionBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {
	public static final String PARAM_PACKAGE_ID = "packageId";
	public static final String PARAM_VERSION = "version";

	private static final String MSG_STATUS_SUCCESSFUL =
			"Support for the version has applied successfully.";
	private static final String MSG_STATUS_FAILURE =
			"Failed to apply support for the version.";

	@Autowired
	private IPackageStore packageStore;
	@Autowired
	private PackageInstallationService packageInstallationService;
	@Autowired
	private FileHelper helper;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
		Long packageId = (Long) request.getData().get(PARAM_PACKAGE_ID);
		Integer version = (Integer) request.getData().get(PARAM_VERSION);
		ServiceResponse response;
		InputStream stream=null;
		try {
			PackageModel pkg = packageStore.findById(packageId);

			stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE,pkg.getName() + PackageFileType.PACKAGE_XML.getDotExtension(), helper.getVersion(), packageId.toString(), version.toString());
			ITranslationResult<Boolean> result = packageInstallationService.supportVersion(stream);
			if (result.getResult() != null && result.getResult()) {
				response = new ServiceResponse(MSG_STATUS_SUCCESSFUL, true);
			} else {
				response = new ServiceResponse(MSG_STATUS_FAILURE, false);
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			response = new ServiceResponse(e.getMessage(), false);
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return response;
	}
}
