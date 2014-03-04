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
