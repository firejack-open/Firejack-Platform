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

package net.firejack.platform.service.directory.broker.directory;

import net.firejack.platform.api.directory.domain.Directory;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IDirectoryStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@TrackDetails
@Component("updateDirectoryPositionBroker")
public class UpdateDirectoryPositionBroker extends ServiceBroker<ServiceRequest<NamedValues>, ServiceResponse> {

	@Autowired
	private IDirectoryStore store;

	@Override
	protected ServiceResponse perform(ServiceRequest<NamedValues> request) throws Exception {
		Long directoryId = (Long) request.getData().get("directoryId");
		Integer newPos = (Integer) request.getData().get("newPosition");
		store.moveDirectory(directoryId, newPos);
		return new ServiceResponse<Directory>("Directory order changed successfully.", true);
	}
}
