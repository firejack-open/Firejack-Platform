package net.firejack.platform.service.deployment.broker;
/*
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


import net.firejack.platform.api.deployment.domain.WarStatus;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@TrackDetails
@Component
public class DeployStatusBroker extends ServiceBroker<ServiceRequest, ServiceResponse<WebArchive>> implements FilenameFilter {

	@Value("${debug.mode}")
	private boolean debug;
	private File webapps = new File(System.getenv("CATALINA_BASE"), "webapps");

	@Override
	protected ServiceResponse<WebArchive> perform(ServiceRequest request) throws Exception {
		if (debug) {
			webapps = new File(System.getenv("CATALINA_HOME"), "webapps");
		}

		String host = ConfigContainer.getHost();
		int port = ConfigContainer.getPort();

		String[] files = webapps.list(this);

		List<WebArchive> archives = new ArrayList<WebArchive>(files.length);

		for (String file : files) {
			String name = file.substring(0,file.indexOf("."));
			String context = name.equals("ROOT") ? "" : ("/" + name);
			String s = String.format("http://%s:%d%s/status/war", host, port, context);
			URL url = new URL(s);

			WebArchive webArchive = new WebArchive(host, name, WarStatus.ERROR);

			URLConnection urlc = url.openConnection();
			urlc.setReadTimeout(1000);
			urlc.setConnectTimeout(1000);

			try {
				int code = ((HttpURLConnection) urlc).getResponseCode();
				if (code == HttpServletResponse.SC_CONFLICT){
					webArchive.setWarStatus(WarStatus.WAIT);
				} else if (code == HttpServletResponse.SC_OK){
					webArchive.setWarStatus(WarStatus.DONE);
				} else if (code == HttpServletResponse.SC_NOT_FOUND){
					webArchive.setWarStatus(WarStatus.ERROR);
				}
			} catch (SocketTimeoutException e) {
				webArchive.setWarStatus(WarStatus.WAIT);
			} catch (IOException e) {
				webArchive.setWarStatus(WarStatus.ERROR);
			}
			archives.add(webArchive);
		}

		return new ServiceResponse<WebArchive>(archives);
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(".war");
	}
}
