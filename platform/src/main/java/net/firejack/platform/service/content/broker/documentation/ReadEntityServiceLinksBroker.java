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

import net.firejack.platform.api.content.domain.ActionServiceLink;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.service.content.broker.documentation.service.DocumentationService;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("ReadEntityServiceLinksBroker")
public class ReadEntityServiceLinksBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<ActionServiceLink>> {

	@Autowired
	private DocumentationService documentationService;

	@Override
	protected ServiceResponse<ActionServiceLink> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
		Long actionId = request.getData().getIdentifier();
		List<ActionServiceLink> links = documentationService.findLinks(actionId);
		return new ServiceResponse<ActionServiceLink>(links, "Load links successfully.", true);
	}
}
