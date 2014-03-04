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

import net.firejack.platform.api.config.model.ConfigType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IConfigStore;
import net.firejack.platform.core.store.registry.IDatabaseStore;
import net.firejack.platform.core.store.registry.IFileStore;
import net.firejack.platform.core.store.registry.ISystemStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("findByLookupConfigBroker")
public class FindByLookupConfigBroker extends ServiceBroker<ServiceRequest<NamedValues<Object>>, ServiceResponse> {

	@Autowired
	private IConfigStore configStore;
	@Autowired
	private ISystemStore systemStore;
	@Autowired
	private IDatabaseStore databaseStore;
	@Autowired
	private IFileStore filestoreStore;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues<Object>> request) throws Exception {
		String lookup = (String) request.getData().get("lookup");
		ConfigType type = (ConfigType) request.getData().get("type");

		Object result;
		if (type == ConfigType.SYSTEM) {
			result = systemStore.findByLookup(lookup);
		} else if (type == ConfigType.DATABASE) {
			result = databaseStore.findByLookup(lookup);
		} else if (type == ConfigType.FILESTORE) {
			result = filestoreStore.findByLookup(lookup);
		} else {
			result = configStore.findByLookup(lookup);
		}

		if (result != null) {
			AbstractDTO dto = factory.convertTo(type.getClazz(), result);
			return new ServiceResponse(dto, "Find by lookup successfully", true);
		} else {
			return new ServiceResponse("Config not found by lookup", false);
		}
	}
}
