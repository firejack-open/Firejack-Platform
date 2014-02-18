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
