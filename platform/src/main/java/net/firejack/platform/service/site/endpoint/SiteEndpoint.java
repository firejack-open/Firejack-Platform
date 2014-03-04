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

package net.firejack.platform.service.site.endpoint;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.api.site.domain.NavigationElementTree;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component("siteService")
@Path("site")
@WebService()
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public class SiteEndpoint implements ISiteEndpoint {

    @Override
    @GET
    @Path("/navigation/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<NavigationElement> readNavigationElement(@PathParam("id") Long id) {
        return OPFEngine.SiteService.readNavigationElementBroker(id);
    }

    /**
     * Service retrieves tree navigation elements by parent lookup
     *
     * @param lookup
     * @return
     */
    @Override
    @GET
    @Path("/navigation/tree-by-lookup/{lookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<NavigationElement> readTreeNavigationElementsByParentLookup(
		    @PathParam("lookup") String lookup) {
        return OPFEngine.SiteService.readTreeNavigationElementsByParentLookup(lookup);
    }
    
        /**
     * Service retrieves the child navigation elements by parent id.
     *
     * @param parentId - ID of the parent entity
     * @return found list of navigation elements
     */
    @Override
    @GET
    @Path("/navigation/list-by-parent-id/{parentId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<NavigationElement> readNavigationElementsByParentId(
		    @PathParam("parentId") Long parentId) {
        return OPFEngine.SiteService.readNavigationElementsByParentId(parentId);
    }
    
    /**
     * Service create a navigation element
     *
     * @param request request with navigation element data
     * @return created navigation element
     */
    @Override
    @POST
    @Path("/navigation")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<RegistryNodeTree> createNavigationElement(
		    ServiceRequest<NavigationElement> request) {
        return OPFEngine.SiteService.createNavigationElement(request.getData());
    }

    /**
     * Service update the navigation element
     *
     * @param id - navigation element id which need to update
     * @param request - request with navigation element data
     * @return updated navigation element
     */
    @Override
    @PUT
    @Path("/navigation/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<RegistryNodeTree> updateNavigationElement(
		    @PathParam("id") Long id, ServiceRequest<NavigationElement> request) {
        return OPFEngine.SiteService.updateNavigationElement(request.getData());
    }

    /**
     * Service delete the navigation element by id
     *
     * @param id - navigation element id which need to delete
     * @return message
     */
    @Override
    @DELETE
    @Path("/navigation/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteNavigationElement(@PathParam(value = "id") Long id) {
        return OPFEngine.SiteService.deleteNavigationElement(id);
    }

    @Override
    @GET
    @Path("/navigation/cached/{packageLookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<NavigationElement> readCachedNavigationList(
		    @PathParam("packageLookup") String packageLookup) {
        return OPFEngine.SiteService.readCachedNavigationElements(packageLookup);
    }

    @Override
    @GET
    @Path("/navigation/tree/{packageLookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<NavigationElementTree> readTreeNavigationMenu(
		    @PathParam("packageLookup") String packageLookup, @QueryParam("lazyLoadResource") boolean lazyLoadResource) {
        return OPFEngine.SiteService.readTreeNavigationMenu(packageLookup, lazyLoadResource);
    }

}
