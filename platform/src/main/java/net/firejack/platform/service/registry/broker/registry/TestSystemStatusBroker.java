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
