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

package net.firejack.platform.service.filestore.broker.directory;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IFileStore;
import net.firejack.platform.core.utils.OpenFlameSpringContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

/**
 * Class encapsulates the directory removal fnctionality
 */
@Component
@TrackDetails
public class DeleteFileDirectoryBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {

	/**
	 * Removes the directory with the specified path
	 *
	 * @param request - the message passed to the business function with all data required
	 * @return information about the success of the deletion operation
	 * @throws net.firejack.platform.core.exception.BusinessFunctionException
	 *
	 */
	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
		String lookup = (String) request.getData().get("lookup");
		String[] path = (String[]) request.getData().get("path");

		IFileStore fileStore = OpenFlameSpringContext.getBean(lookup);
		fileStore.delete(path);

		return new ServiceResponse("Directory removed successfully.", true);
	}
}