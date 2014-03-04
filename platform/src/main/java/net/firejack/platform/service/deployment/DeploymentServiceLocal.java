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

package net.firejack.platform.service.deployment;


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
