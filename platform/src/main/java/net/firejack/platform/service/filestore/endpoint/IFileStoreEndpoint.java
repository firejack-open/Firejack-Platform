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

package net.firejack.platform.service.filestore.endpoint;

import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.filestore.domain.FileStoreInfo;
import net.firejack.platform.core.response.ServiceResponse;
import org.apache.cxf.interceptor.InFaultInterceptors;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

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
@WebService(endpointInterface = "net.firejack.platform.service.filestore.endpoint.IFileStoreEndpoint")
public interface IFileStoreEndpoint {
	/**
	 * Service creates the directory
	 *
	 * @param path - directory path
	 * @return information about the success of the creation operation
	 */
	@WebMethod
	ServiceResponse createDirectory(@WebParam(name = "lookup") String lookup, @WebParam(name = "path") List<String> path);

	/**
	 * Service removes the directory
	 *
	 * @param path - directory path
	 * @return information about the success of the removal operation
	 */
	@WebMethod
	ServiceResponse deleteDirectory(@WebParam(name = "lookup") String lookup, @WebParam(name = "path") List<String> path);

	/**
	 * Service modifies the directory name
	 *
	 * @param lookup
	 * @param name   - updated name
	 * @param path   - directory path
	 * @return information about the success of the modification operation
	 */
	@WebMethod
	ServiceResponse renameDirectory(@WebParam(name = "lookup") String lookup, @WebParam(name = "name") String name, @WebParam(name = "path") List<String> path);

	/**
	 * Service searches the directory for the file/directory that conform to the search criteria
	 *
	 * @param path - directory path
	 * @param term - term to search by
	 * @return found file/directory
	 */
	@WebMethod
	ServiceResponse<FileInfo> search(@WebParam(name = "lookup") String lookup, @WebParam(name = "path") List<String> path, @WebParam(name = "term") String term);

	/**
	 * Service retrieves the file
	 *
	 * @param lookup
	 * @param filename - file name
	 * @param path     - file path
	 * @return file to be downloaded
	 */
	@WebMethod(operationName = "download")
	ServiceResponse<FileInfo> download0(@WebParam(name = "lookup") String lookup, @WebParam(name = "filename") String filename, @WebParam(name = "path") List<String> path);

	/**
	 * Service updates the file
	 *
	 * @param path - file path
	 * @param data - new file content
	 * @return information about the success of the modification operation
	 */
	@WebMethod
	ServiceResponse upload(@WebParam(name = "lookup") String lookup, @WebParam(name = "path") List<String> path, @WebParam(name = "data") FileInfo data);

	/**
	 * Service removes the file
	 *
	 * @param lookup
	 * @param filename - file name
	 * @param path     - file path
	 * @return information about the success of the deletion operation
	 */
	@WebMethod
	ServiceResponse deleteFile(@WebParam(name = "lookup") String lookup, @WebParam(name = "filename") String filename, @WebParam(name = "path") List<String> path);

	/**
	 * Service reads information about the filestore
	 *
	 * @return filestore value object
	 */
	@WebMethod
	ServiceResponse<FileStoreInfo> readFileStoreInfo();

	@WebMethod
	ServiceResponse<FileInfo> getInfo(@WebParam(name = "lookup") String lookup, @WebParam(name = "path") List<String> path);

	@WebMethod
	ServiceResponse unzip(@WebParam(name = "lookup") String lookup, @WebParam(name = "path") List<String> path, @WebParam(name = "data") FileInfo info);
}
