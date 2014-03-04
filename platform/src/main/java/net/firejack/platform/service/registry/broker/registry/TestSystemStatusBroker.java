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

package net.firejack.platform.service.registry.broker.registry;

import net.firejack.platform.api.registry.domain.CheckUrl;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.RegistryNodeProtocol;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.OpenFlameDataSource;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

@TrackDetails
@Component("testSystemStatusBroker")
public class TestSystemStatusBroker extends ServiceBroker<ServiceRequest<CheckUrl>, ServiceResponse<CheckUrl>> {

	@Override
	protected ServiceResponse<CheckUrl> perform(ServiceRequest<CheckUrl> request) throws Exception {
		CheckUrl checkUrl = request.getData();
		String message;
		if (urlValidator(checkUrl)) {
			message = "Test Url has gone successfully";
			checkUrl.setStatus(RegistryNodeStatus.ACTIVE.name());
		} else {
			message = "Test Url has gone failure";
			checkUrl.setStatus(RegistryNodeStatus.INACTIVE.name());
		}
		return new ServiceResponse<CheckUrl>(checkUrl, message, true);
	}

	private boolean urlValidator(CheckUrl checkUrl) {
		try {
			boolean connected;

			RegistryNodeProtocol protocol = checkUrl.getProtocol();
			String serverName = checkUrl.getServerName();
			Integer port = checkUrl.getPort();
			String parentPath = checkUrl.getParentPath();
			String urlPath = checkUrl.getUrlPath();
			if (RegistryNodeProtocol.JDBC.equals(protocol)) {
				logger.info("Trying to connect to db...");
				connected = OpenFlameDataSource.ping(checkUrl.getRdbms(), serverName, port.toString(), checkUrl.getUsername(), checkUrl.getPassword(), parentPath, urlPath);
			} else if (protocol == null || StringUtils.isBlank(urlPath)) {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(serverName, port), 5000);
				connected = socket.isConnected();
				socket.close();
			} else {
				final StringBuilder builder = new StringBuilder(protocol.getProtocol());

				if (StringUtils.isNotBlank(serverName)) {
					builder.append(serverName);
				} else {
					return false;
				}
				if (port != null) {
					builder.append(":").append(port);
				}
				if (StringUtils.isNotBlank(parentPath)) {
					builder.append(parentPath);
				}

				builder.append(urlPath);

				HttpURLConnection.setFollowRedirects(false);
				HttpURLConnection con = (HttpURLConnection) new URL(builder.toString()).openConnection();
				con.setReadTimeout(1000);
				con.setConnectTimeout(1000);
				connected = con.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST;
				con.disconnect();
			}
			return connected;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return false;
		}
	}
}
