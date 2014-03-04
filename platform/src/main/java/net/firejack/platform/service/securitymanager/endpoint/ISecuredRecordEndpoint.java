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

package net.firejack.platform.service.securitymanager.endpoint;

import net.firejack.platform.api.securitymanager.domain.MoveSecuredRecordInfo;
import net.firejack.platform.api.securitymanager.domain.SecuredRecord;
import net.firejack.platform.api.securitymanager.domain.SecuredRecordNode;
import net.firejack.platform.api.securitymanager.domain.TreeNodeSecuredRecord;
import net.firejack.platform.core.domain.IdLookup;
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
@WebService(endpointInterface = "net.firejack.platform.service.securitymanager.endpoint.ISecuredRecordEndpoint")
public interface ISecuredRecordEndpoint {
	@WebMethod
	ServiceResponse<SecuredRecord> readSecuredRecordByEntityIdAndType(
			@WebParam(name = "entityId") Long entityId,
			@WebParam(name = "typeLookup") String typeLookup);

	@WebMethod
	ServiceResponse<SecuredRecord> createSecuredRecord(
			@WebParam(name = "parentId") Long parentId,
			@WebParam(name = "parentType") String parentType,
			@WebParam(name = "request") ServiceRequest<SecuredRecord> request);

    @WebMethod
    ServiceResponse createSecuredRecords(
            @WebParam(name = "request") ServiceRequest<TreeNodeSecuredRecord> request);

	@WebMethod
	ServiceResponse<SecuredRecordNode> readAllSecuredRecords();

	@WebMethod
	ServiceResponse<SecuredRecord> moveSecuredRecord(@WebParam(name = "request") ServiceRequest<MoveSecuredRecordInfo> request);

	@WebMethod
	ServiceResponse updateSecuredRecord(@WebParam(name = "request") ServiceRequest<SecuredRecord> request);

	@WebMethod
	ServiceResponse deleteSecuredRecord(
            @WebParam(name = "entityId") Long entityId,@WebParam(name = "typeLookup") String typeLookup,
            @WebParam(name = "entityCustomId") String entityCustomId);

    @WebMethod
    ServiceResponse deleteSecuredRecords(@WebParam(name = "request") ServiceRequest<IdLookup> request);

}
