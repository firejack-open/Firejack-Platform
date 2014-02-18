package net.firejack.platform.core.config.installer;
/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */


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
