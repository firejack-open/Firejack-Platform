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

import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.validation.annotation.DomainType;
import net.firejack.platform.core.validation.process.ValidateUnique;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;

@Component
@TrackDetails
public class CheckNameBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {

    @Autowired
   	private ValidateUnique validateUnique;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
        NamedValues data = request.getData();
        String path = (String) data.get("path");
        String name = (String) data.get("name");
        DomainType type = (DomainType) data.get("type");

        name = URLDecoder.decode(name, "UTF-8");
        if (validateUnique.checkName(path, name, type)) {
            return new ServiceResponse("Check successfully", true);
        } else {
            return new ServiceResponse("Name is not unique", false);
        }
    }
}
