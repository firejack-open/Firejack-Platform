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

package net.firejack.platform.service.deployment.endpoint;


import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.deployment.domain.Deployed;
import net.firejack.platform.api.deployment.domain.NavigationChanges;
import net.firejack.platform.api.deployment.domain.PackageChange;
import net.firejack.platform.api.deployment.domain.WebArchive;
import net.firejack.platform.core.response.ServiceResponse;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path("deployment")
public class DeploymentEndpoint implements IDeploymentEndpoint {

	@GET
	@Path("/deploy/{packageId}/{name}/{file}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse deploy(@PathParam("packageId") Long packageId, @PathParam("name") String name, @PathParam("file") String file) {
		return OPFEngine.DeploymentService.deploy(packageId, name, file);
	}

	@GET
	@Path("/undeploy/{name}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse undeploy(@PathParam("name") String name) {
		return OPFEngine.DeploymentService.undeploy(name);
	}

	@GET
	@Path("/list")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Deployed> list() {
		return OPFEngine.DeploymentService.list();
	}

	@GET
	@Path("/status")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<WebArchive> status() {
		return OPFEngine.DeploymentService.status();
	}

	@GET
	@Path("/restart")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse restart() {
		return OPFEngine.DeploymentService.restart();
	}

	@GET
	@Path("/changes/{packageLookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<PackageChange> changes(@PathParam("packageLookup") String packageLookup) {
		return OPFEngine.DeploymentService.changes(packageLookup);
	}

    @GET
    @Path("/last-changes/{timestamp}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<NavigationChanges> lastNavigationChanges(@PathParam("timestamp") Long timestamp) {
        return OPFEngine.DeploymentService.lastNavigationChanges(timestamp);
    }

}
