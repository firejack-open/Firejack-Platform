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

package net.firejack.platform.core.config.installer;


import net.firejack.platform.api.deployment.IDeploymentService;
import net.firejack.platform.api.deployment.domain.Deployed;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IServerStore;
import net.firejack.platform.core.store.registry.ISystemStore;
import net.firejack.platform.core.utils.OpenFlame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestTaskService {

	@Autowired
	private MultiTaskExecutor multiTaskExecutor;
	@Autowired
	private ISystemStore systemStore;
	@Autowired
	private IServerStore serverStore;

	public void deploy(final Long packageId, final String name, final String file) {
		multiTaskExecutor.execute(packageId, false, new Task<IDeploymentService>() {
			@Override
			public ServiceResponse execute(IDeploymentService service) {
				return service.deploy(packageId, name, file);
			}
		});
	}

	public void undeploy(Long packageId, final String name) {
		multiTaskExecutor.execute(packageId, false, new Task<IDeploymentService>() {
			@Override
			public ServiceResponse execute(IDeploymentService service) {
				return service.undeploy(name);
			}
		});
	}

	public List<ServiceResponse<Deployed>> list(Long systemId) {
		List<ServerModel> servers = serverStore.findChildrenByParentId(systemId, null);
		return multiTaskExecutor.execute(servers, true, new Task<IDeploymentService>() {
			@Override
			public ServiceResponse<Deployed> execute(IDeploymentService service) {
				return service.list();
			}
		});
	}

	public List<ServiceResponse<WebArchive>> status(Long systemId) {
		List<ServerModel> servers = serverStore.findChildrenByParentId(systemId, null);
		return multiTaskExecutor.execute(servers, true, new Task<IDeploymentService>() {
			@Override
			public ServiceResponse<WebArchive> execute(IDeploymentService service) {
				return service.status();
			}
		});
	}

	public void restart() {
		SystemModel system = systemStore.findByLookup(OpenFlame.SYSTEM);
		List<ServerModel> servers = serverStore.findChildrenByParentId(system.getId(), null);
		multiTaskExecutor.execute(servers, true, new Task<IDeploymentService>() {
			@Override
			public ServiceResponse execute(IDeploymentService service) {
				return service.restart();
			}
		});
	}
}
