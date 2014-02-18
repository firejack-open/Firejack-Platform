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
package net.firejack.platform.service.config.endpoint;

import net.firejack.platform.api.config.domain.Config;
import net.firejack.platform.api.config.model.ConfigType;
import net.firejack.platform.core.domain.ListLookup;
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
@WebService(endpointInterface = "net.firejack.platform.service.config.endpoint.IConfigEndpoint")
public interface IConfigEndpoint {
	@WebMethod
	ServiceResponse<Config> readConfig(@WebParam(name = "id") Long id);

	@WebMethod
	ServiceResponse findByLookup(@WebParam(name = "lookup") String lookup, @WebParam(name = "type") ConfigType type);

    @WebMethod
    ServiceResponse<Config> findListByLookup(@WebParam(name = "request") ServiceRequest<ListLookup> request);

	@WebMethod
	ServiceResponse<Config> readAllConfigsByRegistryNodeId(@WebParam(name = "registryNodeId") Long registryNodeId);

	@WebMethod
	ServiceResponse<Config> searchConfig(@WebParam(name = "configId") Long parentId, @WebParam(name = "term") String term);

	@WebMethod
	ServiceResponse<Config> createConfig(@WebParam(name = "request") ServiceRequest<Config> request);

	@WebMethod
	ServiceResponse<Config> createBatchConfig(@WebParam(name = "request") ServiceRequest<Config> request);

	@WebMethod
	ServiceResponse<Config> updateConfig(@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<Config> request);

	@WebMethod
	ServiceResponse<Config> deleteConfig(@WebParam(name = "id") Long id);

}
