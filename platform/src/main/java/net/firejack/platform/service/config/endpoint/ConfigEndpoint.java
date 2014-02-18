/**
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
package net.firejack.platform.service.config.endpoint;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.config.model.ConfigType;
import net.firejack.platform.core.domain.ListLookup;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path("config")
public class ConfigEndpoint implements IConfigEndpoint {

	/**
	 * Read config by id
	 *
	 * @param id config id
	 *
	 * @return founded config
	 */
	@GET
	@Path("/config/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Config> readConfig(@PathParam("id") Long id) {
		return OPFEngine.ConfigService.readConfig(id);
	}

	/**
	 * Read config by lookup
	 *
	 * @param lookup lookup
	 * @param type   config type
	 *
	 * @return founded type
	 */
	@GET
	@Path("/config/by-lookup/{lookup}/{type}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Config> findByLookup(@PathParam("lookup") String lookup, @PathParam("type") ConfigType type) {
		return OPFEngine.ConfigService.findByLookup(lookup, type);
	}

    @POST
    @Path("/config/list/by-lookup")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Config> findListByLookup(ServiceRequest<ListLookup> request) {
        return OPFEngine.ConfigService.findListByLookup(request.getData());
    }


	//    READ-ALL-BY-REGISTRY-NODE-ID-REQUEST:
//    /rest/registry/authority/permission/node/{registryNodeId} --> GET method

	/**
	 * Read all by parent id
	 *
	 * @param registryNodeId parent id
	 *
	 * @return founded configs
	 */
	@GET
	@Path("/config/node/{registryNodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Config> readAllConfigsByRegistryNodeId(@PathParam("registryNodeId") Long registryNodeId) {
		return OPFEngine.ConfigService.readAllConfigsByRegistryNodeId(registryNodeId);
	}

	/**
	 * Search configs  by parent and term expression
	 *
	 * @param parentId parent id
	 * @param term     term expression
	 *
	 * @return founded configs
	 */
	@GET
	@Path("/config/node/search/{configId}/{term}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Config> searchConfig(@PathParam("configId") Long parentId, @PathParam("term") String term) {
		return OPFEngine.ConfigService.searchConfig(parentId, term);
	}

	/**
	 * Create new config
	 *
	 * @param request config data
	 *
	 * @return created config
	 */
	@POST
	@Path("/config")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Config> createConfig(ServiceRequest<Config> request) {
		return OPFEngine.ConfigService.createConfig(request.getData());
	}

	@POST
	@Path("/config/batch")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Config> createBatchConfig(ServiceRequest<Config> request) {
		return OPFEngine.ConfigService.createBatchConfig(request.getDataList());
	}

	/**
	 * Update config by id
	 *
	 * @param id config id
	 * @param request  config config
	 *
	 * @return updated config
	 */
	@PUT
	@Path("/config/{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Config> updateConfig(@PathParam("id") Long id, ServiceRequest<Config> request) {
		return OPFEngine.ConfigService.updateConfig(id, request.getData());
	}

	/**
	 * Delete config by id
	 *
	 * @param id config id
	 *
	 * @return deleted config
	 */
	@DELETE
	@Path("/config/{configId}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Config> deleteConfig(@PathParam(value = "configId") Long id) {
		return OPFEngine.ConfigService.deleteConfig(id);
	}
}
