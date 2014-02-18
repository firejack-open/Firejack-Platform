/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.service.filestore.broker.file;

import net.firejack.platform.api.filestore.domain.FileStoreInfo;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;

/** Class encapsulates reading the filestore functionality */
@Component
@TrackDetails
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ReadFileStoreBroker extends ServiceBroker<ServiceRequest, ServiceResponse<FileStoreInfo>> {

	/**
	 * Reads the filestore
	 *
	 *
	 * @param request - the message passed to the business function with all data required
	 *
	 * @return information about the operation success and filestore value object
	 *
	 * @throws net.firejack.platform.core.exception.BusinessFunctionException
	 *
	 */
	@Override
	protected ServiceResponse<FileStoreInfo> perform(ServiceRequest request) throws Exception {
		File filestore = FileUtils.getTempDirectory();
		return new ServiceResponse<FileStoreInfo>(new FileStoreInfo(filestore.getUsableSpace(), filestore.getTotalSpace(), filestore.getTotalSpace() - filestore.getUsableSpace()), "Filestore info read successfully.", true);

	}
}