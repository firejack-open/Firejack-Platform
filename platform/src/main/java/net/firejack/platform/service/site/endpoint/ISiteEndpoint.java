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

import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.api.site.domain.NavigationElementTree;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import org.apache.cxf.interceptor.InFaultInterceptors;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@SOAPBinding(style = SOAPBinding.Style.RPC)
@InInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingInInterceptor",
		"org.apache.cxf.binding.soap.saaj.SAAJInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameAuthorizingInInterceptor"})
@OutInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingOutInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JOutInterceptor"})
@InFaultInterceptors(interceptors = "org.apache.cxf.interceptor.LoggingOutInterceptor")
@WebService(endpointInterface = "net.firejack.platform.service.site.endpoint.ISiteEndpoint")
public interface ISiteEndpoint {

    @WebMethod
	ServiceResponse<NavigationElement> readNavigationElement(@WebParam(name = "id") Long id);

	/**
	 * Service retrieves tree navigation elements by parent lookup
	 *
	 * @param lookup
	 *
	 * @return
	 */
	@WebMethod
	ServiceResponse<NavigationElement> readTreeNavigationElementsByParentLookup(@WebParam(name = "lookup") String lookup);

	/**
	 * Service retrieves the child navigation elements by parent id.
	 *
	 * @param parentId - ID of the parent entity
	 *
	 * @return found list of navigation elements
	 */
	@WebMethod
	ServiceResponse<NavigationElement> readNavigationElementsByParentId(@WebParam(name = "parentId") Long parentId);

	/**
	 * Service create a navigation element
	 *
	 * @param request request with navigation element data
	 *
	 * @return created navigation element
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createNavigationElement(@WebParam(name = "request") ServiceRequest<NavigationElement> request);

	/**
	 * Service update the navigation element
	 *
	 * @param id - navigation element id which need to update
	 * @param request      - request with navigation element data
	 *
	 * @return updated navigation element
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateNavigationElement(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<NavigationElement> request);

	/**
	 * Service delete the navigation element by id
	 *
	 * @param id - navigation element id which need to delete
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse deleteNavigationElement(@WebParam(name = "id") Long id);

	@WebMethod
	ServiceResponse<NavigationElement> readCachedNavigationList(@WebParam(name = "packageLookup") String packageLookup);

    @WebMethod
    ServiceResponse<NavigationElementTree> readTreeNavigationMenu(@WebParam(name = "packageLookup") String packageLookup, @WebParam(name = "lazyLoadResource") boolean lazyLoadResource);
}
