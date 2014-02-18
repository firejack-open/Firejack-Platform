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
