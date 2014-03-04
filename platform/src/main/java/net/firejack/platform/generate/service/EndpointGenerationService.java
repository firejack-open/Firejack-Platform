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

package net.firejack.platform.generate.service;

import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.generate.beans.web.api.Api;
import net.firejack.platform.generate.beans.web.api.LocalService;
import net.firejack.platform.generate.beans.web.endpoint.Endpoint;
import net.firejack.platform.generate.structure.Structure;
import net.firejack.platform.web.mina.annotations.ProgressStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class EndpointGenerationService extends BaseGeneratorService implements IEndpointGenerationService {

    @ProgressStatus(weight = 1, description = "Generate endpoint service  objects")
    public void generationEndpoint(IPackageDescriptor descriptor, Api api, Structure structure) throws IOException {

        List<LocalService> locals = api.getLocals();
        if (locals != null) {
            for (LocalService local : locals) {
                Endpoint endpoint = new Endpoint(local);
                endpoint.setApi(api.getName());
                endpoint.addImport(api);
                endpoint.generateWebService();

                api.addEndpoint(endpoint);

                generate(endpoint, "templates/code/server/service/endpoint.vsl", structure.getSrc());
                generate(endpoint.getWebService(), "templates/code/server/service/webservice.vsl", structure.getSrc());
            }
        }
    }
}
