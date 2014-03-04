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