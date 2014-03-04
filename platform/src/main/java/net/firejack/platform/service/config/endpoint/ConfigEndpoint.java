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
