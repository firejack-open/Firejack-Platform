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

package net.firejack.platform.api.deployment;


import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.deployment.domain.Deployed;
import net.firejack.platform.api.deployment.domain.NavigationChanges;
import net.firejack.platform.api.deployment.domain.PackageChange;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.core.response.ServiceResponse;

public class DeploymentServiceProxy extends AbstractServiceProxy implements IDeploymentService {

	public DeploymentServiceProxy(Class[] classes) {
		super(classes);
	}

	@Override
	public String getServiceUrlSuffix() {
		return "/deployment";
	}

	@Override
	public ServiceResponse deploy(Long packageId, String name, String file) {
        return get("/deploy/" + packageId + "/" + name + "/" + file);
    }

	@Override
	public ServiceResponse undeploy(String name) {
		return get("/undeploy/" + name);
	}

	@Override
	public ServiceResponse<Deployed> list() {
		return get("/list");
	}

	@Override
	public ServiceResponse<WebArchive> status() {
		return get("/status");
	}

	@Override
	public ServiceResponse restart() {
		return get("/restart");
	}

    @Override
    public ServiceResponse<PackageChange> changes(String  packageLookup) {
        return get("/changes/" + packageLookup);
    }

    @Override
    public ServiceResponse<NavigationChanges> lastNavigationChanges(Long timestamp) {
        return get("/last-changes/" + timestamp);
    }

}
