package net.firejack.platform.service.deployment;
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


import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.deployment.IDeploymentService;
import net.firejack.platform.api.deployment.domain.Deployed;
import net.firejack.platform.api.deployment.domain.NavigationChanges;
import net.firejack.platform.api.deployment.domain.PackageChange;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.service.deployment.broker.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(APIConstants.BEAN_NAME_DEPLOYMENT_SERVICE)
public class DeploymentServiceLocal implements IDeploymentService {

	@Autowired
	private DeployBroker deployBroker;
	@Autowired
	private UnDeployBroker unDeployBroker;
	@Autowired
	private DeployListBroker deployListBroker;
	@Autowired
	private DeployStatusBroker deployStatusBroker;
	@Autowired
	private RestartBroker restartBroker;
    @Autowired
    private PackageChangesBroker packageChangesBroker;
    @Autowired
    private LastNavigationChangesBroker lastNavigationChangesBroker;

	@Override
	public ServiceResponse deploy(Long packageId, String name, String file) {
		NamedValues values = new NamedValues();
		values.put("packageId", packageId);
		values.put("name", name);
		values.put("file", file);
		return deployBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse undeploy(String name) {
		NamedValues values = new NamedValues();
		values.put("name", name);
		return unDeployBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse<Deployed> list() {
		return deployListBroker.execute(new ServiceRequest());
	}

	@Override
	public ServiceResponse<WebArchive> status() {
		return deployStatusBroker.execute(new ServiceRequest());
	}

	@Override
	public ServiceResponse restart() {
		return restartBroker.execute(new ServiceRequest());
	}

    @Override
    public ServiceResponse<PackageChange> changes(String packageLookup) {
        return packageChangesBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(packageLookup)));
    }

    @Override
    public ServiceResponse<NavigationChanges> lastNavigationChanges(Long timestamp) {
        return lastNavigationChangesBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(timestamp)));
    }

}
