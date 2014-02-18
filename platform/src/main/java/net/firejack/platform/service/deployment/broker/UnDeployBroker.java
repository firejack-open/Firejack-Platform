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


import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InetAddress;

@TrackDetails
@Component
public class UnDeployBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {

	@Value("${debug.mode}")
	private boolean debug;
	private File webapps = new File(System.getenv("CATALINA_BASE"), "webapps");

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
		String name = (String) request.getData().get("name");
		String host = InetAddress.getLocalHost().getHostName();

		if (debug) {
			webapps = new File(System.getenv("CATALINA_HOME"), "webapps");
		}

		File app = new File(webapps, name);
		FileUtils.deleteQuietly(app);

		return new ServiceResponse("UnDeploy to server " + host + " successfully", true);
	}
}
