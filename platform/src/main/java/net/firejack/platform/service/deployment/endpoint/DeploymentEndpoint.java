package net.firejack.platform.service.deployment.endpoint;
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
