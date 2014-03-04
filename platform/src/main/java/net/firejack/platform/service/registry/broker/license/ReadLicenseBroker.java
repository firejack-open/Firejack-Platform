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
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.web.security.license.LicenceHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.stereotype.Component;

@TrackDetails
@Component
public class ReadLicenseBroker extends ServiceBroker<ServiceRequest, ServiceResponse<License>> {
    @Override
    protected ServiceResponse<License> perform(ServiceRequest request) throws Exception {
        License license = LicenceHelper.read();
        if (license != null) {
            license.setSignature(null);
        }
        return new ServiceResponse<License>(license, "Load license", true);
    }
}
