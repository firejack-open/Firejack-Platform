package net.firejack.platform.service.content.broker.documentation;

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

@TrackDetails
@Component("GetDocumentationPageBroker")
public class GetDocumentationPageBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<SimpleIdentifier<String>>> {

	@Override
	protected ServiceResponse<SimpleIdentifier<String>> perform(ServiceRequest<NamedValues> request) throws Exception {
		String token = (String) request.getData().get("token");
		String country = (String) request.getData().get("country");
		String lookup = (String) request.getData().get("lookup");
		Integer maxImageWidth = (Integer) request.getData().get("maxImageWidth");
		lookup = lookup.replace("/", ".");

		//TODO temporary solution
		try {
			URL permissionsURL = new URL(Env.FIREJACK_URL.getValue() + "/console/documentation/template");
			HttpURLConnection connection = (HttpURLConnection) permissionsURL.openConnection();
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Cookie", OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE + "=" + token + ";");

			//Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			String urlParameters = "country=" + URLEncoder.encode(country, "UTF-8") +
					"&lookup=" + URLEncoder.encode(lookup, "UTF-8") +
					"&maxImageWidth=" + maxImageWidth;
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			StringBuilder stringBuilder;
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			stringBuilder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}

			return new ServiceResponse<SimpleIdentifier<String>>(new SimpleIdentifier<String>(stringBuilder.toString()), "Load successfully.", true);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}

	}
}
