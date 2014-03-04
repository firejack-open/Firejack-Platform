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


import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.deployment.DeploymentServiceProxy;
import net.firejack.platform.api.deployment.IDeploymentService;
import net.firejack.platform.api.registry.model.LogLevel;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IServerStore;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.mina.aop.ManuallyProgress;
import net.firejack.platform.web.security.model.context.OPFContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MultiTaskExecutor {
	private static final Logger logger = Logger.getLogger(MultiTaskExecutor.class);
	@Autowired
	private IServerStore serverStore;
	@Autowired
	private ManuallyProgress progress;
	@Autowired
	@Qualifier("taskExecutor")
	private TaskExecutor executor;
	private final Object lock = new Object();

	public <T extends AbstractDTO> List<ServiceResponse<T>> execute(Long packageId, boolean sync, Task<IDeploymentService> task) {
		List<IDeploymentService> proxies = load(packageId);
		return execute(sync, proxies, task);
	}

	public <T extends AbstractDTO> List<ServiceResponse<T>> execute(List<ServerModel> servers, boolean sync, Task<IDeploymentService> task) {
		List<IDeploymentService> proxies = load(servers);
		return execute(sync, proxies, task);
	}

	private <T extends AbstractDTO> List<ServiceResponse<T>> execute(boolean sync, final List<IDeploymentService> proxies, final Task<IDeploymentService> task) {
		final List<ServiceResponse<T>> result = new ArrayList<ServiceResponse<T>>();

        if (!proxies.isEmpty()) {
            final OPFContext opfContext = OPFContext.getContext();
            for (final IDeploymentService proxy : proxies) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        OPFContext.saveContext(opfContext);
                        ServiceResponse response;
                        try {
                            response = task.execute(proxy);
                        } catch (Exception e) {
                            response = new ServiceResponse<T>(e.toString(), false);
                        }
                        result.add(response);

                        if (proxies.size() == result.size()) {
                            synchronized (lock) {
                                lock.notify();
                            }
                        }

                        progress.status(response.getMessage(), 5, LogLevel.INFO);
                    }
                });
            }

            if (sync) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        logger.error(e, e);
                    }
                }
            }
        }

		return result;
	}

	private List<IDeploymentService> load(Long packageId) {
		List<ServerModel> servers = serverStore.findByPackageId(packageId);
		return load(servers);
	}

	private List<IDeploymentService> load(List<ServerModel> servers) {
		List<IDeploymentService> proxies = new ArrayList<IDeploymentService>();

		if (servers != null) {
			for (ServerModel server : servers) {
				try {
					String url = WebUtils.getNormalizedUrl(server.getServerName(), server.getPort(), OpenFlameConfig.CONTEXT_URL.getValue());
					DeploymentServiceProxy proxy = (DeploymentServiceProxy)OPFEngine.getRemoteProxy(url, IDeploymentService.class);
					proxies.add(proxy);
				} catch (Exception e) {
					logger.error(e, e);
				}
			}
		}
		return proxies;
	}
}