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

import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IFileStore;
import net.firejack.platform.core.utils.OpenFlameSpringContext;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class encapsulates searching the directory functionality
 */
@Component
@TrackDetails
public class SearchFileStoreBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<FileInfo>> {

	/**
	 * Searches for the directory/file with the specified path by the search term
	 *
	 * @param request - the message passed to the business function with all data required
	 * @return found stored file/directory
	 * @throws net.firejack.platform.core.exception.BusinessFunctionException
	 *
	 */
	@Override
	protected ServiceResponse<FileInfo> perform(ServiceRequest<NamedValues> request) throws Exception {
		String lookup = (String) request.getData().get("lookup");
		String term = (String) request.getData().get("term");
		String[] path = (String[]) request.getData().get("path");

		IFileStore fileStore = OpenFlameSpringContext.getBean(lookup);
		List<FileInfo> dirs = new ArrayList<FileInfo>();

		List<File> files = fileStore.search(Pattern.compile(term), path);

		for (File file : files) {
			dirs.add(new FileInfo(file.getName(), file.lastModified()));
		}

		return new ServiceResponse<FileInfo>(dirs, "Directory search was successful.", true);
	}
}