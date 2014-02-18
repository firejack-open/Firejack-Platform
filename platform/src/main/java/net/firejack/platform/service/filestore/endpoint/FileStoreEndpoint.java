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

import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.MultiPartMediaTypes;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.filestore.domain.FileStoreInfo;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.utils.WebUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Component
@Path("filestore")
public class FileStoreEndpoint implements IFileStoreEndpoint {
	/**
	 * Service creates the directory
	 *
	 * @param path - directory path
	 * @return information about the success of the creation operation
	 */
	@Override
	@POST
	@Path("/directory/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse createDirectory(@PathParam("lookup") String lookup, @QueryParam("path")List<String> path) {
		return OPFEngine.FileStoreService.createDirectory(lookup, path.toArray(new String[path.size()]));
	}

	/**
	 * Service removes the directory
	 *
	 * @param path - directory path
	 * @return information about the success of the removal operation
	 */
	@Override
	@DELETE
	@Path("/directory/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse deleteDirectory(@PathParam("lookup") String lookup, @QueryParam("path") List<String> path) {
		return OPFEngine.FileStoreService.deleteDirectory(lookup, path.toArray(new String[path.size()]));
	}

	/**
	 * Service modifies the directory name
	 *
	 * @param lookup
	 * @param name   - updated name
	 * @param path   - directory path
	 * @return information about the success of the modification operation
	 */
	@Override
	@PUT
	@Path("/directory/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse renameDirectory(@PathParam("lookup") String lookup, @QueryParam("name") String name, @QueryParam("path") List<String> path) {
		return OPFEngine.FileStoreService.renameDirectory(lookup, name, path.toArray(new String[path.size()]));
	}

	/**
	 * Service searches the directory for the file/directory that conform to the search criteria
	 *
	 * @param path - directory path
	 * @param term - term to search by
	 * @return found file/directory
	 */
	@Override
	@GET
	@Path("/directory/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<FileInfo> search(@PathParam("lookup") String lookup, @QueryParam("path") List<String> path, @QueryParam("term") String term) {
		return OPFEngine.FileStoreService.search(lookup, term, path.toArray(new String[path.size()]));
	}

	/**
	 * Service retrieves the file
	 *
	 * @param path     - file path
	 * @param filename - file name
	 * @return file to be downloaded
	 */
	@GET
	@Path("/file/{lookup}/{filename}")
	@Produces(MediaType.WILDCARD)
	public StreamingOutput download(@PathParam("lookup") final String lookup, @PathParam("filename") final String filename, @QueryParam("path") final List<String> path) {
		return new StreamingOutput() {
			public void write(OutputStream output) throws IOException, WebApplicationException {
				InputStream stream = OPFEngine.FileStoreService.download(lookup, filename, path.toArray(new String[path.size()]));
				if (stream != null) {
					IOUtils.copy(stream, output);
					IOUtils.closeQuietly(stream);
				}
			}
		};
	}

	public ServiceResponse<FileInfo> download0(String lookup, String filename, List<String> path) {
		InputStream stream = OPFEngine.FileStoreService.download(lookup, filename, path.toArray(new String[path.size()]));
		return new ServiceResponse<FileInfo>(new FileInfo(filename, stream), "Package archive successfully", true);
	}

	/**
	 * Service updates the file
	 *
	 * @param path        - file path
	 * @param filename    - file name
	 * @param inputStream - new file content
	 * @return information about the success of the modification operation
	 */
	@POST
	@Path("/file/{lookup}/{filename}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public ServiceResponse upload(
			@PathParam("lookup") String lookup,
			@PathParam("filename") String filename,
			@QueryParam("path") List<String> path,
			@FormDataParam("file") InputStream inputStream) {
		return OPFEngine.FileStoreService.upload(lookup, filename, inputStream, path.toArray(new String[path.size()]));
	}

	@Override
	public ServiceResponse upload(String lookup, List<String> path, FileInfo info) {
		return OPFEngine.FileStoreService.upload(lookup, info.getFilename(), info.getStream(), path.toArray(new String[path.size()]));
	}

	/**
	 * Service removes the file
	 *
	 * @param lookup
	 * @param filename - file name
	 * @param path     - file path
	 * @return information about the success of the deletion operation
	 */
	@Override
	@DELETE
	@Path("/file/{lookup}/{filename}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse deleteFile(@PathParam("lookup") String lookup, @PathParam("filename") String filename, @QueryParam("path") List<String> path) {
		return OPFEngine.FileStoreService.deleteFile(lookup, filename, path.toArray(new String[path.size()]));
	}

	/**
	 * Service reads information about the filestore
	 *
	 * @return filestore value object
	 */
	@Override
	@GET
	@Path("/file")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<FileStoreInfo> readFileStoreInfo() {
		return OPFEngine.FileStoreService.readFileStoreInfo();
	}

	@Override
	@GET
	@Path("/file/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<FileInfo> getInfo(@PathParam("lookup") String lookup, @QueryParam("path") List<String> path) {
		return OPFEngine.FileStoreService.getInfo(lookup, path.toArray(new String[path.size()]));
	}

	@POST
	@Path("/zip/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes(MultiPartMediaTypes.MULTIPART_MIXED)
	public ServiceResponse zip(MultiPart multiPart, @PathParam("lookup") String lookup, @QueryParam("path") List<String> path) {
		Map<String, InputStream> stream = WebUtils.getStream(multiPart);
		return OPFEngine.FileStoreService.zip(lookup, stream, path.toArray(new String[path.size()]));
	}

	@PUT
	@Path("/zip/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes(MultiPartMediaTypes.MULTIPART_MIXED)
	public ServiceResponse updatezip(MultiPart multiPart, @PathParam("lookup") String lookup, @QueryParam("path") List<String> path) {
		Map<String, InputStream> stream = WebUtils.getStream(multiPart);
		return OPFEngine.FileStoreService.updatezip(lookup, stream, path.toArray(new String[path.size()]));
	}

	@POST
	@Path("/unzip/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public ServiceResponse unzip(@PathParam("lookup") String lookup, @QueryParam("path") List<String> path,@FormDataParam("file") InputStream stream) {
		return OPFEngine.FileStoreService.unzip(lookup, stream, path.toArray(new String[path.size()]));
	}

    @POST
	@Path("/unzip/temp/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public ServiceResponse<FileInfo> unzipTemp(@PathParam("lookup") String lookup, @QueryParam("path") List<String> path,@FormDataParam("file") InputStream stream) {
		return OPFEngine.FileStoreService.unzipTemp(lookup, stream, path.toArray(new String[path.size()]));
	}

	@Override
	public ServiceResponse unzip(String lookup, List<String> path, FileInfo info) {
		return OPFEngine.FileStoreService.unzip(lookup, info.getStream(), path.toArray(new String[path.size()]));
	}
}
