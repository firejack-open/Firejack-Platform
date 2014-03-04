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

package net.firejack.platform.service.deployment.broker;


import net.firejack.platform.api.deployment.domain.Deployed;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;

@TrackDetails
@Component
public class DeployListBroker extends ServiceBroker<ServiceRequest, ServiceResponse<Deployed>> implements FilenameFilter {

	@Value("${debug.mode}")
	private boolean debug;
	private File webapps = new File(System.getenv("CATALINA_BASE"), "webapps");

	@Override
	protected ServiceResponse<Deployed> perform(ServiceRequest request) throws Exception {
		String host = InetAddress.getLocalHost().getHostName();
		if (debug) {
			webapps = new File(System.getenv("CATALINA_HOME"), "webapps");
		}

		String[] files = webapps.list(this);

		return new ServiceResponse<Deployed>(new Deployed(host, files == null ?
                Collections.<String>emptyList() : Arrays.asList(files)),
                "Deploy list server " + host + " successfully", true);
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(".war");
	}
}
