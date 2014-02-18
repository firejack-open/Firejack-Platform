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
