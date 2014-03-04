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

package net.firejack.platform.service.registry.broker.system;

import net.firejack.platform.api.deployment.domain.Deployed;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.System;
import net.firejack.platform.core.broker.ReadBroker;
import net.firejack.platform.core.config.installer.IDeployService;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.model.registry.system.ServerModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.registry.IServerStore;
import net.firejack.platform.core.store.registry.ISystemStore;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("readSystemBroker")
public class ReadSystemBroker extends ReadBroker<SystemModel, System> {

	@Autowired
	private ISystemStore systemStore;

    @Autowired
    private IServerStore serverStore;

	@Autowired
	private IDeployService deployService;

	@Override
	protected IStore<SystemModel, Long> getStore() {
		return systemStore;
	}

	@Override
	protected System convertToModel(SystemModel entity) {
		System system = super.convertToModel(entity);

        List<ServerModel> serverModels = serverStore.findAllByParentIdWithFilter(system.getId(), null);
		List<Deployed> deployedPackageNames = deployService.getDeployedPackageNames(entity);

        if (deployedPackageNames != null && !deployedPackageNames.isEmpty()) {
            for (Package associatedPackage: system.getAssociatedPackages()) {
                boolean isInstalled = true;
                for (ServerModel serverModel : serverModels) {
                    List<String> wars = findWars(deployedPackageNames, serverModel);
                    if (wars != null && !wars.isEmpty()) {
                        String contextUrl = associatedPackage.getUrlPath();
                        String contextName = contextUrl.replace("/", "");
                        String warName = contextName + ".war";
                        isInstalled = wars.contains(warName);
                    } else {
                        isInstalled = false;
                    }
                    if (!isInstalled) {
                        break;
                    }
                }
                associatedPackage.setDeployed(isInstalled);
            }
            for (Package associatedPackage: system.getAssociatedPackages()) {
                if (OpenFlame.PACKAGE.equals(associatedPackage.getLookup())) {
                    system.getAssociatedPackages().remove(associatedPackage);
                    break;
                }
            }
        }
		return system;
	}

    private List<String> findWars(List<Deployed> deployedPackageNames, ServerModel serverModel) {
        List<String> wars = null;
        for (Deployed deployedPackageName : deployedPackageNames) {
            if (deployedPackageName != null && deployedPackageName.getServer().equals(serverModel.getName())) {
                wars = deployedPackageName.getWars();
            }
        }
        return wars;
    }
}
