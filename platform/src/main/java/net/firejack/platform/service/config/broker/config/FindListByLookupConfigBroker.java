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

package net.firejack.platform.service.config.broker.config;

import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.ListLookup;
import net.firejack.platform.core.model.registry.config.ConfigModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IConfigStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("findListByLookupConfigBroker")
public class FindListByLookupConfigBroker extends ServiceBroker<ServiceRequest<ListLookup>, ServiceResponse<Config>> {

	@Autowired
	private IConfigStore configStore;


	@Override
	protected ServiceResponse<Config> perform(ServiceRequest<ListLookup> request) throws Exception {
		ListLookup listLookup = request.getData();

        List<ConfigModel> configModels = configStore.findListByLookup(listLookup.getLookup());
        List<Config> configs = factory.convertTo(Config.class, configModels);
        return new ServiceResponse<Config>(configs, "Find by lookup successfully", true);
	}
}
