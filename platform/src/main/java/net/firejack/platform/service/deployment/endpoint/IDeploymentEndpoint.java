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


import net.firejack.platform.api.deployment.domain.Deployed;
import net.firejack.platform.api.deployment.domain.NavigationChanges;
import net.firejack.platform.api.deployment.domain.PackageChange;
import net.firejack.platform.api.deployment.domain.WebArchive;
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
@WebService(endpointInterface = "net.firejack.platform.service.deployment.endpoint.IDeploymentEndpoint")
public interface IDeploymentEndpoint {

	@WebMethod
	ServiceResponse deploy(@WebParam(name = "packageId") Long packageId, @WebParam(name = "name") String name, @WebParam(name = "file") String file);

	@WebMethod
	ServiceResponse undeploy(@WebParam(name = "name") String name);

	@WebMethod
	ServiceResponse<Deployed> list();

	@WebMethod
	ServiceResponse<WebArchive> status();

	@WebMethod
	ServiceResponse restart();

    @WebMethod
    ServiceResponse<PackageChange> changes(@WebParam(name = "packageLookup") String  packageLookup);

    @WebMethod
    ServiceResponse<NavigationChanges> lastNavigationChanges(@WebParam(name = "timestamp") Long timestamp);

}
