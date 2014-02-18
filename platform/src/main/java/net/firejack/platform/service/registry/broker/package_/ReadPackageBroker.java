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

package net.firejack.platform.service.registry.broker.package_;

import net.firejack.platform.api.deployment.domain.Deployed;
import net.firejack.platform.api.registry.domain.Package;
import net.firejack.platform.api.registry.domain.PackageVersion;
import net.firejack.platform.core.broker.ReadBroker;
import net.firejack.platform.core.config.installer.IDeployService;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.model.registry.domain.SystemModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.registry.IPackageStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.service.registry.helper.PackageVersionHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@TrackDetails
@Component("readPackageBroker")
public class ReadPackageBroker extends ReadBroker<PackageModel, Package> {

	@Autowired
	private IPackageStore store;

	@Autowired
	private PackageVersionHelper packageVersionHelper;

	@Autowired
	private IDeployService deployService;

	@Override
	protected IStore<PackageModel, Long> getStore() {
		return store;
	}

	@Override
	protected ServiceResponse<Package> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
		ServiceResponse<Package> perform = super.perform(request);

		Package item = perform.getItem();

		PackageModel model = factory.convertFrom(PackageModel.class, item);
		PackageVersion packageVersion = packageVersionHelper.populatePackageVersion(model);
		item.setPackageVersion(packageVersion);

        Boolean deployed = null;
        SystemModel systemModel = model.getSystem();
        if (systemModel != null) {
            List<Deployed> deployedPackageNames = deployService.getDeployedPackageNames(systemModel);

            String contextUrl = item.getUrlPath();
            String contextName = contextUrl.replace("/", "");
            String warName = (StringUtils.isBlank(contextName) ? "ROOT" : contextName) + ".war";

            deployed = false;
            if (deployedPackageNames != null && !deployedPackageNames.isEmpty()) {
                for (Deployed packageName : deployedPackageNames) {
	                if (packageName != null) {
		                List<String> wars = packageName.getWars();
		                if(!deployed && wars != null && wars.size()!=0){
		                    for (String war : wars) {
		                        if (war.equals(warName)){
		                            deployed = true;
		                            break;
		                        }
		                    }
		                }
	                }
                }
            }
        }
		item.setDeployed(deployed);
		return perform;
	}

    @Override
    protected PackageModel getEntity(Long id) {
        return store.findWithSystemAndDatabaseById(id);
    }
}
