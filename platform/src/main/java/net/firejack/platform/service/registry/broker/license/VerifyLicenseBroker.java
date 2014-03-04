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

package net.firejack.platform.service.registry.broker.license;

import net.firejack.platform.api.registry.domain.License;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.web.security.license.LicenceHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@TrackDetails
@Component
public class VerifyLicenseBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse<License>> {

	@Override
	protected ServiceResponse<License> perform(ServiceRequest<NamedValues> request) throws Exception {
		InputStream stream = (InputStream) request.getData().get("stream");

        try {
            License license = FileUtils.readJAXB(License.class, stream);
            LicenceHelper.verify(license);
            LicenceHelper.save(license);
        } catch (Exception e) {
            return new ServiceResponse<License>(e.getMessage(), false);
        }

        return new ServiceResponse<License>("Import xml successfully", true);
	}
}
