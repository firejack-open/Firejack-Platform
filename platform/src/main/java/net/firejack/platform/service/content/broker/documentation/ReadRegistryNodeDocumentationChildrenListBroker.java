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
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.*;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("ReadRegistryNodeDocumentationChildrenListBroker")
public class ReadRegistryNodeDocumentationChildrenListBroker extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<Lookup>> {

	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	@Override
	protected ServiceResponse<Lookup> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
		Long registryNodeId = (Long) request.getData().getIdentifier();
        if (registryNodeId == 0) {
            registryNodeId = null;
        }
	    List<RegistryNodeModel> registryNodes = registryNodeStore.findChildrenByParentIdAndTypes(registryNodeId, null,
			    RootDomainModel.class,
			    DomainModel.class,
			    SystemModel.class,
			    PackageModel.class,
			    EntityModel.class,
			    ActionModel.class);

	    List<Lookup> registryNodeVOs = factory.convertTo(Lookup.class, registryNodes);
		return new ServiceResponse<Lookup>(registryNodeVOs, "Load successfully.", true);
	}
}
