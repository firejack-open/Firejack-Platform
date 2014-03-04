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
