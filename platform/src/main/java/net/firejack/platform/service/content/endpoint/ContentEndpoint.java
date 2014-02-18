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

package net.firejack.platform.service.content.endpoint;


import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.*;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.ListLookup;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.DownloadUtils;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.SortOrder;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Component("contentService")
@Path("content")
public class ContentEndpoint implements IContentEndpoint {

    @Context
    private HttpHeaders headers;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//   FOLDER SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves the folder
     *
     * @param id - ID of the folder
     * @return found folder
     */
    @GET
    @Path("/folder/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Folder> readFolder(@PathParam("id") Long id) {
        return OPFEngine.ContentService.readFolder(id);
    }

    /**
     * Service retrieves the child folders by parent id.
     *
     * @param parentId - ID of the parent entity
     * @return found list of folders
     */
    @GET
    @Path("/folder/list-by-parent-id/{parentId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Folder> readFoldersByParentId(
             @PathParam("parentId") Long parentId) {
        return OPFEngine.ContentService.readFoldersByParentId(parentId);
    }

    /**
     * Service retrieves the child folders by parent lookup.
     *
     * @param lookup - lookup of the parent entity
     * @return found list of folders
     */
    @GET
    @Path("/folder/list-by-lookup/{lookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Folder> readFoldersByLikeLookup(
             @PathParam("lookup") String lookup) {
        return OPFEngine.ContentService.readFoldersByLikeLookup(lookup);
    }

    /**
     * Service create a folder
     *
     * @param request - request with folder data
     * @return created folder
     */
    @POST
    @Path("/folder")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<RegistryNodeTree> createFolder(ServiceRequest<Folder> request) {
        return OPFEngine.ContentService.createFolder(request.getData());
    }

    /**
     * Service update the folder
     *
     * @param id - folder id which need to update
     * @param request - request with folder data
     * @return updated folder
     */
    @PUT
    @Path("/folder/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<RegistryNodeTree> updateFolder(
             @PathParam("id") Long id, ServiceRequest<Folder> request) {
        return OPFEngine.ContentService.updateFolder(request.getData());
    }

    /**
     * Service delete the folder by id
     *
     * @param folderId - folder id which need to delete
     * @return deleted folder
     */
    @DELETE
    @Path("/folder/{folderId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Folder> deleteFolder(
             @PathParam(value = "folderId") Long folderId) {
        return OPFEngine.ContentService.deleteFolder(folderId);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    COLLECTION SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves the collection
     *
     * @param id - ID of the collection
     * @return found collection
     */
    @GET
    @Path("/collection/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Collection> readCollection(@PathParam("id") Long id) {
        return OPFEngine.ContentService.readCollection(id);
    }

    /**
     * Service retrieves the child collections by parent id.
     *
     * @param parentId - ID of the parent entity
     * @return found list of collections
     */
    @GET
    @Path("/collection/list-by-parent-id/{parentId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Collection> readCollectionsByParentId(
             @PathParam("parentId") Long parentId) {
        return OPFEngine.ContentService.readCollectionsByParentId(parentId);
    }

    /**
     * Service retrieves the child collections by like lookup.
     *
     * @param lookup for search
     * @return list of collections
     */
    @GET
    @Path("/collection/list-by-lookup/{lookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Collection> readCollectionsByLikeLookup(
             @PathParam("lookup") String lookup) {
        return OPFEngine.ContentService.readCollectionsByLikeLookup(lookup);
    }

    /**
     * Service create a collection
     *
     * @param request request with collection data
     * @return created collection
     */
    @POST
    @Path("/collection")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<RegistryNodeTree> createCollection(
             ServiceRequest<Collection> request) {
        return OPFEngine.ContentService.createCollection(request.getData());
    }

    /**
     * Service update the collection
     *
     * @param id - collection id which need to update
     * @param request - request with collection data
     * @return updated collection
     */
    @PUT
    @Path("/collection/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<RegistryNodeTree> updateCollection(
             @PathParam("id") Long id, ServiceRequest<Collection> request) {
        return OPFEngine.ContentService.updateCollection(request.getData());
    }

    /**
     * Service delete the collection by id
     *
     * @param id - collection id which need to delete
     * @return deleted collection
     */
    @DELETE
    @Path("/collection/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteCollection(@PathParam(value = "id") Long id) {
        return OPFEngine.ContentService.deleteCollection(id);
    }

    /**
     * Service export the collection by id with all sub collections, all resources and folders.
     *
     * @param id collection ID which need to export to file
     * @return response with exported collection stream
     */
    @GET
    @Path("/collection/export/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    //todo: not valid in terms of OPF action abstraction
    public Response exportCollectionArchiveFile(@PathParam("id") final Long id) {
        ServiceResponse<FileInfo> response = OPFEngine.ContentService.exportCollectionArchiveFile(id);
        if (response.isSuccess()) {
            Response.ResponseBuilder responseBuilder = Response.ok(response.getItem().getStream());
            responseBuilder = responseBuilder.type(MediaType.APPLICATION_OCTET_STREAM);
            FileInfo fileInfo = response.getItem();
            String contentDisposition;
            try {
                contentDisposition = DownloadUtils.getContentDisposition(
                        fileInfo.getFilename(), headers.getRequestHeaders().get(HttpHeaders.USER_AGENT).get(0));
            } catch (UnsupportedEncodingException e) {
                contentDisposition = "attachment; filename=\"" + fileInfo.getFilename() + "\"";
            }
            responseBuilder = responseBuilder.header("Content-Disposition", contentDisposition);
            responseBuilder = responseBuilder.header("OPF-Filename", fileInfo.getFilename());
            responseBuilder = responseBuilder.header("OPF-OrgFilename", fileInfo.getOrgFilename());
            responseBuilder = responseBuilder.header("OPF-Updated", fileInfo.getUpdated());
            return responseBuilder.build();
        }
        return null;
    }

	public ServiceResponse<FileInfo> exportCollectionArchiveFileWS(Long id) {
		return  OPFEngine.ContentService.exportCollectionArchiveFile(id);
	}

	/**
     * Service import the content archive file which contains collections, folders and resources.
     *
     * @param inputStream of imported content archive file
     * @return results about imported process
     */
    @POST
    @Path("/collection/import")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //todo: not valid in terms of OPF action abstraction
    public Response importCollectionArchiveFile(
            @FormDataParam("file") InputStream inputStream) {
	    ServiceResponse<FileInfo> response = OPFEngine.ContentService.importCollectionArchiveFile(inputStream);

        String responderData;
        try {
            responderData = WebUtils.serializeObjectToJSON(response);
        } catch (IOException e) {
            throw new BusinessFunctionException(e);
        }

        return Response.ok(responderData).type(MediaType.TEXT_HTML_TYPE).build();
    }

    //todo: not valid in terms of OPF action abstraction
	public ServiceResponse<FileInfo> importCollectionArchiveFile(FileInfo info) {
		return OPFEngine.ContentService.importCollectionArchiveFile(info.getStream());
	}

	/**
	 * Swap position collection members
	 *
	 * @param id collection
	 * @param firstRefId member reference id
	 * @param secondRefId member reference id
	 * @return complete result
	 */
	@GET
	@Path("/collection/swap/{id}/{firstRefId}/{secondRefId}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse swapCollectionMemberships(
			 @PathParam(value = "id") Long id,
			 @PathParam(value = "firstRefId") Long firstRefId,
			 @PathParam(value = "secondRefId") Long secondRefId) {
		return OPFEngine.ContentService.swapCollectionMemberships(id, firstRefId, secondRefId);
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//     RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves resources by lookup list
     *
     * @return found image resource
     */
    @POST
    @Path("/resource/by-lookup-list")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<AbstractResource> readResourcesByLookupList(ServiceRequest<ListLookup> request) {
        return OPFEngine.ContentService.readResourcesByLookupList(request.getData());
    }

    /**
     * Service retrieves the list of all cultures.
     *
     * @return list of cultures
     */
    @GET
    @Path("/resource/culture")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Culture> readAllCultures() {
        return OPFEngine.ContentService.readAllCultures();
    }

    /**
     * Service retrieves the list of available cultures.
     *
     * @param nodeId ID of entity for which need to get available cultures
     * @return list of cultures
     */
    @GET
    @Path("/resource/culture/{nodeId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Culture> readAvailableCultures(
             @PathParam("nodeId") Long nodeId) {
        return OPFEngine.ContentService.readAvailableCultures(nodeId);
    }


    /**
     * Service retrieves the list of resources by parent id.
     *
     * @param registryNodeId ID of parent entity
     * @param exceptIds list of ids which need to exclude from results
     * @param offset start position
     * @param limit count of results
     * @param sortColumn sort column name
     * @param sortDirection sort direction ASC or DESC
     * @return list of resources
     */
    @GET
    @Path("/resource/node/{registryNodeId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Resource> readResourcesByParentId(
             @PathParam("registryNodeId") Long registryNodeId,
             @QueryParam("exceptIds") List<Long> exceptIds,
             @QueryParam("start") Integer offset,
             @QueryParam("limit") Integer limit,
             @QueryParam("sort") String sortColumn,
             @QueryParam("dir") SortOrder sortDirection) {
		Paging paging = new Paging(offset, limit, sortColumn, sortDirection);
        return OPFEngine.ContentService.readResourcesByParentId(registryNodeId, exceptIds, paging);
    }

    /**
     * This method takes in a search term that will search across name to find any matches.
     * In addition, the method should take in any name / value pairs for explicit filters for the user
     * that correspond to other fields declared for the directory on the query string.
     *
     * @param registryNodeId ID of parent entity
     * @param term it is a search term
     * @return list of found resources
     */
    @GET
    @Path("/resource/node/search/{registryNodeId}/{term}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<Resource> searchResourceList(
             @PathParam("registryNodeId") Long registryNodeId,
             @PathParam("term") String term) {
        return OPFEngine.ContentService.searchResourcesByParentIdAndTerm(registryNodeId, term);
    }

    /**
     * Service method allows to upload resource file like image, audio and video
     *
     *
     * @param resourceType one of the list from ResourceType enum
     * @param customTTL this parameter allows to control time to live value
     * @param inputStream uploaded file stream
     * @param fileDisposition information about uploaded file
     * @return response FileInfo with temporary uploaded file name and additional info about file, like for instance for image: width, height
     * @throws java.io.IOException IO exception
     */
    @POST
    @Path("/resource/upload/{resourceType}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //todo: not valid in terms of OPF action abstraction
    public Response uploadImage(@PathParam("resourceType") String resourceType,
                                @QueryParam("ttl") String customTTL, @FormDataParam("file") InputStream inputStream,
                                @FormDataParam("file") FormDataContentDisposition fileDisposition) throws IOException {

        String originalFilename = fileDisposition.getFileName();
	    ServiceResponse<FileInfo> response = OPFEngine.ContentService.uploadResourceFile(resourceType, customTTL, inputStream, originalFilename);

	    ObjectMapper mapper = new ObjectMapper();
	    String responderData = mapper.writeValueAsString(response);

	    return Response.ok(responderData).type(MediaType.TEXT_HTML_TYPE).build();
    }

    //todo: not valid in terms of OPF action abstraction
    public ServiceResponse<FileInfo> uploadImage(String resourceType, String customTTL, FileInfo data) {
	    return OPFEngine.ContentService.uploadResourceFile(
                resourceType, customTTL, data.getStream(), data.getOrgFilename());
    }

    /**
     * Service returns temporary uploaded file by temporary filename
     *
     * @param temporaryFilename temporary filename which
     * @return uploaded file input stream
     */
    @GET
    @Path("/resource/tmp/{temporaryFilename}")
    @Produces(MediaType.WILDCARD)
    //todo: not valid in terms of OPF action abstraction
    public Response getTmpUploadedImage(@PathParam("temporaryFilename") String temporaryFilename) {
        ServiceResponse<FileInfo> response = OPFEngine.ContentService.getTemporaryUploadedFile(temporaryFilename);
        if (response.isSuccess()) {
            return Response.ok(response.getItem().getStream(), MediaType.APPLICATION_OCTET_STREAM).build();
        }
        return null;
    }

    public ServiceResponse<FileInfo> getTmpUploadedImageWS(String temporaryFilename) {
        return OPFEngine.ContentService.getTemporaryUploadedFile(temporaryFilename);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    TEXT RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves text resource by ID for last version and american culture
     *
     * @param id ID of text resource
     * @return found text resource
     */
    @GET
    @Path("/resource/text/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<TextResource> readTextResourceById(@PathParam("id") Long id) {
        return OPFEngine.ContentService.readTextResourceById(id);
    }

    /**
     * Service retrieves text resource by ID
     *
     * @param resourceId ID of text resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found text resource
     */
    @GET
    @Path("/resource/text/by-id-culture-version/{resourceId}/{culture}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<TextResource> readTextResourceByIdCultureAndVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("culture") String culture,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.readTextResourceByIdCultureAndVersion(resourceId, culture, version);
    }

    /**
     * Service retrieves text resource by lookup
     *
     * @param lookup lookup of text resource
     * @return found text resource
     */
    @GET
    @Path("/resource/text/by-lookup/{lookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<TextResource> readTextResourceByLookup(
             @PathParam("lookup") String lookup) {
        return OPFEngine.ContentService.readTextResourceByLookup(lookup);
    }

    /**
     * Service allows to create a new text resource
     *
     * @param request request with text resource data
     * @return created text resource
     */
    @POST
    @Path("/resource/text")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<TextResource> createTextResource(ServiceRequest<TextResource> request) {
        return OPFEngine.ContentService.createTextResource(request.getData());
    }

    /**
     * Service allows to create a copy text resource versions from the last version but increase version number
     *
     * @param resourceId ID of text resource
     * @return text resource with new text resource version
     */
    @POST
    @Path("/resource/text/version/{resourceId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<TextResource> createNewTextResourceVersionForLastVersion(
             @PathParam("resourceId") Long resourceId) {
        return OPFEngine.ContentService.createNewTextResourceVersions(resourceId, null);
    }

    /**
     * Service allows to create copy text resource versions from defined version but increase version number
     *
     * @param resourceId ID of text resource
     * @param version version number from which need to create new ones
     * @return text resource with new text resource version
     */
    @POST
    @Path("/resource/text/version2/{resourceId}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<TextResource> createNewTextResourceVersionForDefinedVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.createNewTextResourceVersions(resourceId, version);
    }


    /**
     * Service updates text resource
     *
     * @param id ID of text resource
     * @param request text resource which need to update
     * @return updated text resource
     */
    @PUT
    @Path("/resource/text/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<TextResource> updateTextResource(
             @PathParam("id") Long id, ServiceRequest<TextResource> request) {
        return OPFEngine.ContentService.updateTextResource(request.getData());
    }

    /**
     * Service creates text resource version
     *
     * @param resourceLookup ID of text resource version
     * @param request text resource version
     * @return message
     */
    @POST
    @Path("/resource/text/version/new/{resourceLookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<TextResourceVersion> createTextResourceVersion(
             @PathParam("resourceLookup") String resourceLookup,
             ServiceRequest<TextResourceVersion> request) {
        return OPFEngine.ContentService.createTextResourceVersion(resourceLookup, request.getData());
    }

    /**
     * Service updates text resource version
     *
     * @param id ID of text resource version
     * @param request text resource version
     * @return message
     */
    @PUT
    @Path("/resource/text/version/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<TextResourceVersion> updateTextResourceVersion(
             @PathParam("id") Long id, ServiceRequest<TextResourceVersion> request) {
        return OPFEngine.ContentService.updateTextResourceVersion(request.getData());
    }

    /**
     * Service deletes text resource with all versions
     *
     * @param id ID of text resource
     * @return message
     */
    @DELETE
    @Path("/resource/text/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteTextResource(@PathParam("id") Long id) {
        return OPFEngine.ContentService.deleteTextResource(id);
    }

    /**
     * Service deletes text resource version
     *
     * @param resourceId ID of text resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    @DELETE
    @Path("/resource/text/version/{resourceId}/{version}/{culture}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteTextResourceVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version,
             @PathParam("culture") String culture) {
        return OPFEngine.ContentService.deleteTextResourceVersion(resourceId, culture, version);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    HTML RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves html resource by ID for last version and american culture
     *
     * @param id ID of html resource
     * @return found html resource
     */
    @GET
    @Path("/resource/html/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<HtmlResource> readHtmlResourceById(@PathParam("id") Long id) {
        return OPFEngine.ContentService.readHtmlResourceById(id);
    }

    /**
     * Service retrieves html resource by ID
     *
     * @param resourceId ID of html resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found html resource
     */
    @GET
    @Path("/resource/html/by-id-culture-version/{resourceId}/{culture}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<HtmlResource> readHtmlResourceByIdCultureAndVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("culture") String culture,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.readHtmlResourceByIdCultureAndVersion(resourceId, culture, version);
    }

    /**
     * Service retrieves html resource by lookup
     *
     * @param lookup lookup of html resource
     * @return found html resource
     */
    @GET
    @Path("/resource/html/by-lookup/{lookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<HtmlResource> readHtmlResourceByLookup(
             @PathParam("lookup") String lookup) {
        return OPFEngine.ContentService.readHtmlResourceByLookup(lookup);
    }

    /**
     * Service allows to create a new html resource
     *
     * @param request request with html resource data
     * @return created html resource
     */
    @POST
    @Path("/resource/html")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<HtmlResource> createHtmlResource(ServiceRequest<HtmlResource> request) {
        return OPFEngine.ContentService.createHtmlResource(request.getData());
    }

    /**
     * Service allows to create a copy html resource versions from the last version but increase version number
     *
     * @param resourceId ID of html resource
     * @return html resource with new html resource version
     */
    @POST
    @Path("/resource/html/version/{resourceId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<HtmlResource> createNewHtmlResourceVersionForLastVersion(
             @PathParam("resourceId") Long resourceId) {
        return OPFEngine.ContentService.createNewHtmlResourceVersions(resourceId, null);
    }

    /**
     * Service allows to create copy html resource versions from defined version but increase version number
     *
     * @param resourceId ID of html resource
     * @param version version number from which need to create new ones
     * @return html resource with new html resource version
     */
    @POST
    @Path("/resource/html/version2/{resourceId}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<HtmlResource> createNewHtmlResourceVersionForDefinedVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.createNewHtmlResourceVersions(resourceId, version);
    }


    /**
     * Service updates html resource
     *
     * @param id ID of html resource
     * @param request html resource which need to update
     * @return updated html resource
     */
    @PUT
    @Path("/resource/html/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<HtmlResource> updateHtmlResource(
             @PathParam("id") Long id, ServiceRequest<HtmlResource> request) {
        return OPFEngine.ContentService.updateHtmlResource(request.getData());
    }

    /**
     * Service updates html resource version
     *
     * @param resourceVersionId ID of html resource version
     * @param request html resource version
     * @return message
     */
    @PUT
    @Path("/resource/html/version/{resourceVersionId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse updateHtmlResourceVersion(
             @PathParam("resourceVersionId") Long resourceVersionId,
             ServiceRequest<HtmlResourceVersion> request) {
        return OPFEngine.ContentService.updateHtmlResourceVersion(request.getData());
    }

    /**
     * Service deletes html resource with all versions
     *
     * @param id ID of html resource
     * @return message
     */
    @DELETE
    @Path("/resource/html/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteHtmlResource(@PathParam("id") Long id) {
        return OPFEngine.ContentService.deleteHtmlResource(id);
    }

    /**
     * Service deletes html resource version
     *
     * @param resourceId ID of html resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    @DELETE
    @Path("/resource/html/version/{resourceId}/{version}/{culture}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteHtmlResourceVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version,
             @PathParam("culture") String culture) {
        return OPFEngine.ContentService.deleteHtmlResourceVersion(resourceId, culture, version);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    IMAGE RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves image resource by ID for last version and american culture
     *
     * @param id ID of image resource
     * @return found image resource
     */
    @GET
    @Path("/resource/image/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ImageResource> readImageResourceById(@PathParam("id") Long id) {
        return OPFEngine.ContentService.readImageResourceById(id);
    }

    /**
     * Service retrieves image resource by ID
     *
     * @param resourceId ID of image resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found image resource
     */
    @GET
    @Path("/resource/image/by-id-culture-version/{resourceId}/{culture}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ImageResource> readImageResourceByIdCultureAndVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("culture") String culture,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.readImageResourceByIdCultureAndVersion(resourceId, culture, version);
    }

    /**
     * Service retrieves image resource by lookup
     *
     * @param lookup lookup of image resource
     * @return found image resource
     */
    @GET
    @Path("/resource/image/by-lookup/{lookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ImageResource> readImageResourceByLookup(
             @PathParam("lookup") String lookup) {
        return OPFEngine.ContentService.readImageResourceByLookup(lookup);
    }

	@GET
	@Path("/resource/image/stream/{lookup}")
	@Produces("image/*")
	public Response readResourceImageStream(@PathParam("lookup") final String lookup) {
        InputStream image = OPFEngine.ContentService.readResourceImageStream(lookup);
        Response.ResponseBuilder responseBuilder = Response.ok(image).type("image/png");
        return responseBuilder.build();
//		return new StreamingOutput() {
//			public void write(OutputStream output) throws IOException, WebApplicationException {
//				InputStream image = OPFEngine.ContentService.readResourceImageStream(lookup);
//				if (image != null) {
//					IOUtils.copy(image, output);
//					IOUtils.closeQuietly(image);
//				}
//			}
//		};
	}

	/**
     * Service retrieves image resource file
     *
     * @param resourceId lookup of image resource
     * @param imageFilename temporary file name of uploaded image
     * @return found image resource
     */
    @GET
    @Path("/resource/image/by-filename/{resourceId}/{imageFilename}")
    @Produces("image/*")
    //todo: not valid in terms of OPF action abstraction
    public Response getImageResourceVersionFile(
            @PathParam("resourceId") Long resourceId,
            @PathParam("imageFilename") String imageFilename) {
        ServiceResponse<FileInfo> response = OPFEngine.ContentService.getImageResourceVersionFile(resourceId, imageFilename);
        if (response.isSuccess()) {
            Response.ResponseBuilder responseBuilder = Response.ok(response.getItem().getStream());
            FileInfo fileInfo = response.getItem();
            String extension = "";
            String mt = "image/png";
            String orgFilename = fileInfo.getOrgFilename();
            if (StringUtils.isNotBlank(orgFilename)) {
                String[] filenames = orgFilename.split("\\.");
                if (filenames.length == 2) {
                    extension = "." + filenames[1];
                }
                MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
                mimetypesFileTypeMap.addMimeTypes("image/png png");
                mt = mimetypesFileTypeMap.getContentType(orgFilename);
            }
            responseBuilder = responseBuilder.type(mt);
            String downloadFilename = fileInfo.getFilename() + extension;
            responseBuilder = responseBuilder.header("OPF-Filename", downloadFilename);
            responseBuilder = responseBuilder.header("OPF-OrgFilename", orgFilename);
            responseBuilder = responseBuilder.header("OPF-Updated", fileInfo.getUpdated());
            return responseBuilder.build();
        }
        return null;
    }

    public ServiceResponse<FileInfo> getImageResourceVersionFileWS(Long resourceId,  String imageFilename) {
        return OPFEngine.ContentService.getImageResourceVersionFile(resourceId, imageFilename);
    }

    /**
     * Service allows to create a new image resource
     *
     * @param request request with image resource data
     * @return created image resource
     */
    @POST
    @Path("/resource/image")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ImageResource> createImageResource(ServiceRequest<ImageResource> request) {
        return OPFEngine.ContentService.createImageResource(request.getData());
    }

    /**
     * Service allows to create a copy image resource versions from the last version but increase version number
     *
     * @param resourceId ID of image resource
     * @return image resource with new image resource version
     */
    @POST
    @Path("/resource/image/version/{resourceId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ImageResource> createNewImageResourceVersionForLastVersion(
             @PathParam("resourceId") Long resourceId) {
        return OPFEngine.ContentService.createNewImageResourceVersions(resourceId, null);
    }

    /**
     * Service allows to create copy image resource versions from defined version but increase version number
     *
     * @param resourceId ID of image resource
     * @param version version number from which need to create new ones
     * @return image resource with new image resource version
     */
    @POST
    @Path("/resource/image/version2/{resourceId}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ImageResource> createNewImageResourceVersionForDefinedVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.createNewImageResourceVersions(resourceId, version);
    }

    /**
     * Service updates image resource
     *
     * @param id ID of image resource
     * @param request image resource which need to update
     * @return updated image resource
     */
    @PUT
    @Path("/resource/image/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ImageResource> updateImageResource(
             @PathParam("id") Long id, ServiceRequest<ImageResource> request) {
        return OPFEngine.ContentService.updateImageResource(request.getData());
    }

    /**
     * Service creates image resource version
     *
     * @param resourceLookup ID of image resource version
     * @param request image resource version
     * @return message
     */
    @POST
    @Path("/resource/image/version/new-by-lookup/{resourceLookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ImageResourceVersion> createImageResourceVersionByLookup(
             @PathParam("resourceLookup") String resourceLookup,
             ServiceRequest<ImageResourceVersion> request) {
        return OPFEngine.ContentService.createImageResourceVersionByLookup(resourceLookup, request.getData());
    }

    /**
     * Service creates image resource version
     *
     * @param resourcePath path of image resource version
     * @param request image resource version
     * @return message
     */
    @POST
    @Path("/resource/image/version/new-by-path/{resourcePath}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ImageResourceVersion> createImageResourceVersionByPath(
             @PathParam("resourcePath") String resourcePath,
             ServiceRequest<ImageResourceVersion> request) {
        return OPFEngine.ContentService.createImageResourceVersionByPath(resourcePath, request.getData());
    }

    /**
     * Service updates image resource version
     *
     * @param resourceVersionId ID of image resource version
     * @param request image resource version
     * @return message
     */
    @PUT
    @Path("/resource/image/version/{resourceVersionId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ImageResourceVersion> updateImageResourceVersion(
             @PathParam("resourceVersionId") Long resourceVersionId,
             ServiceRequest<ImageResourceVersion> request) {
        return OPFEngine.ContentService.updateImageResourceVersion(request.getData());
    }

    /**
     * Service updates image resource version
     *
     * @param resourceLookup lookup of image resource version
     * @param request image resource version
     * @return message
     */
    @PUT
    @Path("/resource/image/version/by-lookup/{resourceLookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<ImageResourceVersion> updateImageResourceVersionByLookup(
             @PathParam("resourceLookup") String resourceLookup,
             ServiceRequest<ImageResourceVersion> request) {
        return OPFEngine.ContentService.updateImageResourceVersionByLookup(resourceLookup, request.getData());
    }

    /**
     * Service deletes image resource with all versions
     *
     * @param id ID of image resource
     * @return message
     */
    @DELETE
    @Path("/resource/image/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteImageResource(@PathParam("id") Long id) {
        return OPFEngine.ContentService.deleteImageResource(id);
    }

    /**
     * Service deletes image resource with all versions
     *
     * @param lookup lookup of image resource
     * @return message
     */
    @DELETE
    @Path("/resource/image/by-lookup/{lookup}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteImageResourceByLookup(@PathParam("lookup") String lookup) {
        return OPFEngine.ContentService.deleteImageResourceByLookup(lookup);
    }

    /**
     * Service deletes image resource version
     *
     * @param resourceId ID of image resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    @DELETE
    @Path("/resource/image/version/{resourceId}/{version}/{culture}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteImageResourceVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version,
             @PathParam("culture") String culture) {
        return OPFEngine.ContentService.deleteImageResourceVersion(resourceId, culture, version);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    AUDIO RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves audio resource by ID for last version and american culture
     *
     * @param id ID of audio resource
     * @return found audio resource
     */
    @GET
    @Path("/resource/audio/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<AudioResource> readAudioResourceById(@PathParam("id") Long id) {
        return OPFEngine.ContentService.readAudioResourceById(id);
    }

    /**
     * Service retrieves audio resource by ID
     *
     * @param resourceId ID of audio resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found audio resource
     */
    @GET
    @Path("/resource/audio/by-id-culture-version/{resourceId}/{culture}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<AudioResource> readAudioResourceByIdCultureAndVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("culture") String culture,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.readAudioResourceByIdCultureAndVersion(resourceId, culture, version);
    }

    /**
     * Service retrieves audio resource by lookup
     *
     * @param lookup lookup of audio resource
     * @return found audio resource
     */
    @GET
    @Path("/resource/audio/by-lookup/{lookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<AudioResource> readAudioResourceByLookup(
             @PathParam("lookup") String lookup) {
        return OPFEngine.ContentService.readAudioResourceByLookup(lookup);
    }

    /**
     * Service retrieves audio resource file
     *
     * @param resourceId lookup of audio resource
     * @param audioFilename temporary file name of uploaded audio
     * @return found audio resource
     */
    @GET
    @Path("/resource/audio/by-filename/{resourceId}/{audioFilename}")
    @Produces("audio/*")
    //todo: not valid in terms of OPF action abstraction
    public Response getAudioResourceVersionFile(
            @PathParam("resourceId") Long resourceId,
            @PathParam("audioFilename") String audioFilename) {
        ServiceResponse<FileInfo> response = OPFEngine.ContentService.getAudioResourceVersionFile(resourceId, audioFilename);
        if (response.isSuccess()) {
            Response.ResponseBuilder responseBuilder = Response.ok(response.getItem().getStream());
            FileInfo fileInfo = response.getItem();
            String extension = "";
            String mt = MediaType.APPLICATION_OCTET_STREAM;
            String orgFilename = fileInfo.getOrgFilename();
            if (StringUtils.isNotBlank(orgFilename)) {
                String[] filenames = orgFilename.split("\\.");
                if (filenames.length == 2) {
                    extension = "." + filenames[1];
                }
                mt = new MimetypesFileTypeMap().getContentType(orgFilename);
            }
            responseBuilder = responseBuilder.type(mt);
            String downloadFilename = fileInfo.getFilename() + extension;
            String contentDisposition;
            try {
                contentDisposition = DownloadUtils.getContentDisposition(
                        orgFilename, headers.getRequestHeaders().get(HttpHeaders.USER_AGENT).get(0));
            } catch (UnsupportedEncodingException e) {
                contentDisposition = "attachment; filename=\"" + orgFilename + "\"";
            }
            responseBuilder = responseBuilder.header("Content-Disposition", contentDisposition);
            responseBuilder = responseBuilder.header("OPF-Filename", downloadFilename);
            responseBuilder = responseBuilder.header("OPF-OrgFilename", orgFilename);
            responseBuilder = responseBuilder.header("OPF-Updated", fileInfo.getUpdated());
            return responseBuilder.build();
        }
        return null;
    }

    public ServiceResponse<FileInfo> getAudioResourceVersionFileWS(Long resourceId, String audioFilename) {
        return OPFEngine.ContentService.getAudioResourceVersionFile(resourceId, audioFilename);
    }

    /**
     * Service allows to create a new audio resource
     *
     * @param request request with audio resource data
     * @return created audio resource
     */
    @POST
    @Path("/resource/audio")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<AudioResource> createAudioResource(
             ServiceRequest<AudioResource> request) {
        return OPFEngine.ContentService.createAudioResource(request.getData());
    }

    /**
     * Service allows to create a copy audio resource versions from the last version but increase version number
     *
     * @param resourceId ID of audio resource
     * @return audio resource with new audio resource version
     */
    @POST
    @Path("/resource/audio/version/{resourceId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<AudioResource> createNewAudioResourceVersionForLastVersion(
             @PathParam("resourceId") Long resourceId) {
        return OPFEngine.ContentService.createNewAudioResourceVersions(resourceId, null);
    }

    /**
     * Service allows to create copy audio resource versions from defined version but increase version number
     *
     * @param resourceId ID of audio resource
     * @param version version number from which need to create new ones
     * @return audio resource with new audio resource version
     */
    @POST
    @Path("/resource/audio/version2/{resourceId}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<AudioResource> createNewAudioResourceVersionForDefinedVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.createNewAudioResourceVersions(resourceId, version);
    }

    /**
     * Service updates audio resource
     *
     * @param id ID of audio resource
     * @param request audio resource which need to update
     * @return updated audio resource
     */
    @PUT
    @Path("/resource/audio/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<AudioResource> updateAudioResource(
             @PathParam("id") Long id, ServiceRequest<AudioResource> request) {
        return OPFEngine.ContentService.updateAudioResource(request.getData());
    }

    /**
     * Service updates audio resource version
     *
     * @param id ID of audio resource version
     * @param request audio resource version
     * @return message
     */
    @PUT
    @Path("/resource/audio/version/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse updateAudioResourceVersion(
             @PathParam("id") Long id, ServiceRequest<AudioResourceVersion> request) {
        return OPFEngine.ContentService.updateAudioResourceVersion(request.getData());
    }

    /**
     * Service deletes audio resource with all versions
     *
     * @param id ID of audio resource
     * @return message
     */
    @DELETE
    @Path("/resource/audio/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteAudioResource(@PathParam("id") Long id) {
        return OPFEngine.ContentService.deleteAudioResource(id);
    }

    /**
     * Service deletes audio resource version
     *
     * @param resourceId ID of audio resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    @DELETE
    @Path("/resource/audio/version/{resourceId}/{version}/{culture}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteAudioResourceVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version,
             @PathParam("culture") String culture) {
        return OPFEngine.ContentService.deleteAudioResourceVersion(resourceId, culture, version);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    VIDEO RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves video resource by ID for last version and american culture
     *
     * @param id ID of video resource
     * @return found video resource
     */
    @GET
    @Path("/resource/video/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<VideoResource> readVideoResourceById(@PathParam("id") Long id) {
        return OPFEngine.ContentService.readVideoResourceById(id);
    }

    /**
     * Service retrieves video resource by ID
     *
     * @param resourceId ID of video resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found video resource
     */
    @GET
    @Path("/resource/video/by-id-culture-version/{resourceId}/{culture}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<VideoResource> readVideoResourceByIdCultureAndVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("culture") String culture,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.readVideoResourceByIdCultureAndVersion(resourceId, culture, version);
    }

    /**
     * Service retrieves video resource by lookup
     *
     * @param lookup lookup of video resource
     * @return found video resource
     */
    @GET
    @Path("/resource/video/by-lookup/{lookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<VideoResource> readVideoResourceByLookup(
             @PathParam("lookup") String lookup) {
        return OPFEngine.ContentService.readVideoResourceByLookup(lookup);
    }

    /**
     * Service retrieves video resource file
     *
     * @param resourceId lookup of video resource
     * @param videoFilename temporary file name of uploaded video
     * @return found video resource
     */
    @GET
    @Path("/resource/video/by-filename/{resourceId}/{videoFilename}")
    @Produces("video/*")
    //todo: not valid in terms of OPF action abstraction
    public Response getVideoResourceVersionFile(
            @PathParam("resourceId") Long resourceId,
            @PathParam("videoFilename") String videoFilename) {
        ServiceResponse<FileInfo> response = OPFEngine.ContentService.getVideoResourceVersionFile(resourceId, videoFilename);
        if (response.isSuccess()) {
            Response.ResponseBuilder responseBuilder = Response.ok(response.getItem().getStream());
            FileInfo fileInfo = response.getItem();
            String extension = "";
            String mt = MediaType.APPLICATION_OCTET_STREAM;
            String orgFilename = fileInfo.getOrgFilename();
            if (StringUtils.isNotBlank(orgFilename)) {
                String[] filenames = orgFilename.split("\\.");
                if (filenames.length == 2) {
                    extension = "." + filenames[1];
                }
                mt = new MimetypesFileTypeMap().getContentType(orgFilename);
            }
            responseBuilder = responseBuilder.type(mt);
            String downloadFilename = fileInfo.getFilename() + extension;
            String contentDisposition;
            try {
                contentDisposition = DownloadUtils.getContentDisposition(
                        orgFilename, headers.getRequestHeaders().get(HttpHeaders.USER_AGENT).get(0));
            } catch (UnsupportedEncodingException e) {
                contentDisposition = "attachment; filename=\"" + orgFilename + "\"";
            }
            responseBuilder = responseBuilder.header("Content-Disposition", contentDisposition);
            responseBuilder = responseBuilder.header("OPF-Filename", downloadFilename);
            responseBuilder = responseBuilder.header("OPF-OrgFilename", orgFilename);
            responseBuilder = responseBuilder.header("OPF-Updated", fileInfo.getUpdated());
            return responseBuilder.build();
        }
        return null;
    }

    public ServiceResponse<FileInfo> getVideoResourceVersionFileWS(Long resourceId,  String videoFilename) {
        return OPFEngine.ContentService.getVideoResourceVersionFile(resourceId, videoFilename);
    }

    /**
     * Service allows to create a new video resource
     *
     * @param request request with video resource data
     * @return created video resource
     */
    @POST
    @Path("/resource/video")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<VideoResource> createVideoResource(
             ServiceRequest<VideoResource> request) {
        return OPFEngine.ContentService.createVideoResource(request.getData());
    }

    /**
     * Service allows to create a copy video resource versions from the last version but increase version number
     *
     * @param resourceId ID of video resource
     * @return video resource with new video resource version
     */
    @POST
    @Path("/resource/video/version/{resourceId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<VideoResource> createNewVideoResourceVersionForLastVersion(
             @PathParam("resourceId") Long resourceId) {
        return OPFEngine.ContentService.createNewVideoResourceVersions(resourceId, null);
    }

    /**
     * Service allows to create copy video resource versions from defined version but increase version number
     *
     * @param resourceId ID of video resource
     * @param version version number from which need to create new ones
     * @return video resource with new video resource version
     */
    @POST
    @Path("/resource/video/version2/{resourceId}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<VideoResource> createNewVideoResourceVersionForDefinedVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.createNewVideoResourceVersions(resourceId, version);
    }

    /**
     * Service updates video resource
     *
     * @param id ID of video resource
     * @param request video resource which need to update
     * @return updated video resource
     */
    @PUT
    @Path("/resource/video/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<VideoResource> updateVideoResource(
             @PathParam("id") Long id, ServiceRequest<VideoResource> request) {
        return OPFEngine.ContentService.updateVideoResource(request.getData());
    }

    /**
     * Service updates video resource version
     *
     * @param id ID of video resource version
     * @param request video resource version
     * @return message
     */
    @PUT
    @Path("/resource/video/version/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse updateVideoResourceVersion(
             @PathParam("id") Long id, ServiceRequest<VideoResourceVersion> request) {
        return OPFEngine.ContentService.updateVideoResourceVersion(request.getData());
    }

    /**
     * Service deletes video resource with all versions
     *
     * @param id ID of video resource
     * @return message
     */
    @DELETE
    @Path("/resource/video/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteVideoResource(@PathParam("id") Long id) {
        return OPFEngine.ContentService.deleteVideoResource(id);
    }

    /**
     * Service deletes video resource version
     *
     * @param resourceId ID of video resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    @DELETE
    @Path("/resource/video/version/{resourceId}/{version}/{culture}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteVideoResourceVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version,
             @PathParam("culture") String culture) {
        return OPFEngine.ContentService.deleteVideoResourceVersion(resourceId, culture, version);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    DOCUMENT RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves document resource by ID for last version and american culture
     *
     * @param id ID of document resource
     * @return found document resource
     */
    @GET
    @Path("/resource/document/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<DocumentResource> readDocumentResourceById(@PathParam("id") Long id) {
        return OPFEngine.ContentService.readDocumentResourceById(id);
    }

    /**
     * Service retrieves document resource by ID
     *
     * @param resourceId ID of document resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found document resource
     */
    @GET
    @Path("/resource/document/by-id-culture-version/{resourceId}/{culture}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<DocumentResource> readDocumentResourceByIdCultureAndVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("culture") String culture,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.readDocumentResourceByIdCultureAndVersion(resourceId, culture, version);
    }

    /**
     * Service retrieves document resource by lookup
     *
     * @param lookup lookup of document resource
     * @return found document resource
     */
    @GET
    @Path("/resource/document/by-lookup/{lookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<DocumentResource> readDocumentResourceByLookup(
             @PathParam("lookup") String lookup) {
        return OPFEngine.ContentService.readDocumentResourceByLookup(lookup);
    }

    /**
     * Service retrieves document resource file
     *
     * @param resourceId lookup of document resource
     * @param documentFilename temporary file name of uploaded document
     * @return found document resource
     */
    @GET
    @Path("/resource/document/by-filename/{resourceId}/{documentFilename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    //todo: not valid in terms of OPF action abstraction
    public Response getDocumentResourceVersionFile(
            @PathParam("resourceId") Long resourceId,
            @PathParam("documentFilename") String documentFilename) {
        ServiceResponse<FileInfo> response = OPFEngine.ContentService.getDocumentResourceVersionFile(resourceId, documentFilename);
        if (response.isSuccess()) {
            Response.ResponseBuilder responseBuilder = Response.ok(response.getItem().getStream());
            FileInfo fileInfo = response.getItem();
            String extension = "";
            String mt = MediaType.APPLICATION_OCTET_STREAM;
            String orgFilename = fileInfo.getOrgFilename();
            if (StringUtils.isNotBlank(orgFilename)) {
                String[] filenames = orgFilename.split("\\.");
                if (filenames.length == 2) {
                    extension = "." + filenames[1];
                }
                mt = new MimetypesFileTypeMap().getContentType(orgFilename);
            }
            responseBuilder = responseBuilder.type(mt);
            String downloadFilename = fileInfo.getFilename() + extension;
            String contentDisposition;
            try {
                contentDisposition = DownloadUtils.getContentDisposition(
                        orgFilename, headers.getRequestHeaders().get(HttpHeaders.USER_AGENT).get(0));
            } catch (UnsupportedEncodingException e) {
                contentDisposition = "attachment; filename=\"" + orgFilename + "\"";
            }
            responseBuilder = responseBuilder.header("Content-Disposition", contentDisposition);
            responseBuilder = responseBuilder.header("OPF-Filename", downloadFilename);
            responseBuilder = responseBuilder.header("OPF-OrgFilename", orgFilename);
            responseBuilder = responseBuilder.header("OPF-Updated", fileInfo.getUpdated());
            return responseBuilder.build();
        }
        return null;
    }

    public ServiceResponse<FileInfo> getDocumentResourceVersionFileWS(Long resourceId,  String documentFilename) {
        return OPFEngine.ContentService.getDocumentResourceVersionFile(resourceId, documentFilename);
    }

    /**
     * Service allows to create a new document resource
     *
     * @param request request with document resource data
     * @return created document resource
     */
    @POST
    @Path("/resource/document")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<DocumentResource> createDocumentResource(
             ServiceRequest<DocumentResource> request) {
        return OPFEngine.ContentService.createDocumentResource(request.getData());
    }

    /**
     * Service allows to create a copy document resource versions from the last version but increase version number
     *
     * @param resourceId ID of document resource
     * @return document resource with new document resource version
     */
    @POST
    @Path("/resource/document/version/{resourceId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<DocumentResource> createNewDocumentResourceVersionForLastVersion(
             @PathParam("resourceId") Long resourceId) {
        return OPFEngine.ContentService.createNewDocumentResourceVersions(resourceId, null);
    }

    /**
     * Service allows to create copy document resource versions from defined version but increase version number
     *
     * @param resourceId ID of document resource
     * @param version version number from which need to create new ones
     * @return document resource with new document resource version
     */
    @POST
    @Path("/resource/document/version2/{resourceId}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<DocumentResource> createNewDocumentResourceVersionForDefinedVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.createNewDocumentResourceVersions(resourceId, version);
    }

    /**
     * Service updates document resource
     *
     * @param id ID of document resource
     * @param request document resource which need to update
     * @return updated document resource
     */
    @PUT
    @Path("/resource/document/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<DocumentResource> updateDocumentResource(
             @PathParam("id") Long id, ServiceRequest<DocumentResource> request) {
        return OPFEngine.ContentService.updateDocumentResource(request.getData());
    }

    /**
     * Service updates document resource version
     *
     * @param id ID of document resource version
     * @param request document resource version
     * @return message
     */
    @PUT
    @Path("/resource/document/version/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse updateDocumentResourceVersion(
             @PathParam("id") Long id, ServiceRequest<DocumentResourceVersion> request) {
        return OPFEngine.ContentService.updateDocumentResourceVersion(request.getData());
    }

    /**
     * Service deletes document resource with all versions
     *
     * @param id ID of document resource
     * @return message
     */
    @DELETE
    @Path("/resource/document/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteDocumentResource(@PathParam("id") Long id) {
        return OPFEngine.ContentService.deleteDocumentResource(id);
    }

    /**
     * Service deletes document resource version
     *
     * @param resourceId ID of document resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    @DELETE
    @Path("/resource/document/version/{resourceId}/{version}/{culture}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteDocumentResourceVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version,
             @PathParam("culture") String culture) {
        return OPFEngine.ContentService.deleteDocumentResourceVersion(resourceId, culture, version);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    FILE RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves file resource by ID for last version and american culture
     *
     * @param id ID of file resource
     * @return found file resource
     */
    @GET
    @Path("/resource/file/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<FileResource> readFileResourceById(@PathParam("id") Long id) {
        return OPFEngine.ContentService.readFileResourceById(id);
    }

    /**
     * Service retrieves file resource by ID
     *
     * @param resourceId ID of file resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found file resource
     */
    @GET
    @Path("/resource/file/by-id-culture-version/{resourceId}/{culture}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<FileResource> readFileResourceByIdCultureAndVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("culture") String culture,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.readFileResourceByIdCultureAndVersion(resourceId, culture, version);
    }

    /**
     * Service retrieves file resource by lookup
     *
     * @param lookup lookup of file resource
     * @return found file resource
     */
    @GET
    @Path("/resource/file/by-lookup/{lookup}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<FileResource> readFileResourceByLookup(
             @PathParam("lookup") String lookup) {
        return OPFEngine.ContentService.readFileResourceByLookup(lookup);
    }

    /**
     * Service retrieves file resource file
     *
     * @param resourceId lookup of file resource
     * @param fileFilename temporary file name of uploaded file
     * @return found file resource
     */
    @GET
    @Path("/resource/file/by-filename/{resourceId}/{fileFilename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    //todo: not valid in terms of OPF action abstraction
    public Response getFileResourceVersionFile(
            @PathParam("resourceId") Long resourceId,
            @PathParam("fileFilename") String fileFilename) {
        ServiceResponse<FileInfo> response = OPFEngine.ContentService.getFileResourceVersionFile(resourceId, fileFilename);
        if (response.isSuccess()) {
            Response.ResponseBuilder responseBuilder = Response.ok(response.getItem().getStream());
            FileInfo fileInfo = response.getItem();
            String extension = "";
            String mt = MediaType.APPLICATION_OCTET_STREAM;
            String orgFilename = fileInfo.getOrgFilename();
            if (StringUtils.isNotBlank(orgFilename)) {
                String[] filenames = orgFilename.split("\\.");
                if (filenames.length == 2) {
                    extension = "." + filenames[1];
                }
                mt = new MimetypesFileTypeMap().getContentType(orgFilename);
            }
            responseBuilder = responseBuilder.type(mt);
            String downloadFilename = fileInfo.getFilename() + extension;
            String contentDisposition;
            try {
                contentDisposition = DownloadUtils.getContentDisposition(
                        orgFilename, headers.getRequestHeaders().get(HttpHeaders.USER_AGENT).get(0));
            } catch (UnsupportedEncodingException e) {
                contentDisposition = "attachment; filename=\"" + orgFilename + "\"";
            }
            responseBuilder = responseBuilder.header("Content-Disposition", contentDisposition);
            responseBuilder = responseBuilder.header("OPF-Filename", downloadFilename);
            responseBuilder = responseBuilder.header("OPF-OrgFilename", orgFilename);
            responseBuilder = responseBuilder.header("OPF-Updated", fileInfo.getUpdated());
            return responseBuilder.build();
        }
        return null;
    }

    public ServiceResponse<FileInfo> getFileResourceVersionFileWS(Long resourceId,  String fileFilename) {
        return OPFEngine.ContentService.getFileResourceVersionFile(resourceId, fileFilename);
    }

    /**
     * Service allows to create a new file resource
     *
     * @param request request with file resource data
     * @return created file resource
     */
    @POST
    @Path("/resource/file")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<FileResource> createFileResource(
             ServiceRequest<FileResource> request) {
        return OPFEngine.ContentService.createFileResource(request.getData());
    }

    /**
     * Service allows to create a copy file resource versions from the last version but increase version number
     *
     * @param resourceId ID of file resource
     * @return file resource with new file resource version
     */
    @POST
    @Path("/resource/file/version/{resourceId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<FileResource> createNewFileResourceVersionForLastVersion(
             @PathParam("resourceId") Long resourceId) {
        return OPFEngine.ContentService.createNewFileResourceVersions(resourceId, null);
    }

    /**
     * Service allows to create copy file resource versions from defined version but increase version number
     *
     * @param resourceId ID of file resource
     * @param version version number from which need to create new ones
     * @return file resource with new file resource version
     */
    @POST
    @Path("/resource/file/version2/{resourceId}/{version}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<FileResource> createNewFileResourceVersionForDefinedVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version) {
        return OPFEngine.ContentService.createNewFileResourceVersions(resourceId, version);
    }

    /**
     * Service updates file resource
     *
     * @param id ID of file resource
     * @param request file resource which need to update
     * @return updated file resource
     */
    @PUT
    @Path("/resource/file/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse<FileResource> updateFileResource(
             @PathParam("id") Long id, ServiceRequest<FileResource> request) {
        return OPFEngine.ContentService.updateFileResource(request.getData());
    }

    /**
     * Service updates file resource version
     *
     * @param id ID of file resource version
     * @param request file resource version
     * @return message
     */
    @PUT
    @Path("/resource/file/version/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse updateFileResourceVersion(
             @PathParam("id") Long id, ServiceRequest<FileResourceVersion> request) {
        return OPFEngine.ContentService.updateFileResourceVersion(request.getData());
    }

    /**
     * Service deletes file resource with all versions
     *
     * @param id ID of file resource
     * @return message
     */
    @DELETE
    @Path("/resource/file/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteFileResource(@PathParam("id") Long id) {
        return OPFEngine.ContentService.deleteFileResource(id);
    }

    /**
     * Service deletes file resource version
     *
     * @param resourceId ID of file resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    @DELETE
    @Path("/resource/file/version/{resourceId}/{version}/{culture}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ServiceResponse deleteFileResourceVersion(
             @PathParam("resourceId") Long resourceId,
             @PathParam("version") Integer version,
             @PathParam("culture") String culture) {
        return OPFEngine.ContentService.deleteFileResourceVersion(resourceId, culture, version);
    }

	/**
	 * Create new documentation
	 *
	 * @param request documentation data
	 *
	 * @return created document
	 */
	@POST
	@Path("/documentation")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<ResourceContent> createDocumentation(ServiceRequest<ResourceContent> request) {
		return OPFEngine.ContentService.createDocumentation(request.getData());
	}

	/**
	 * Update documentation by id
	 *
	 * @param resourceVersionId documentation id
	 * @param request           documentation data
	 *
	 * @return updated documentation
	 */
	@PUT
	@Path("/documentation/{resourceVersionId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<ResourceContent> updateDocumentation(@PathParam("resourceVersionId") Long resourceVersionId, ServiceRequest<ResourceContent> request) {
		return OPFEngine.ContentService.updateDocumentation(resourceVersionId, request.getData());
	}

	/**
	 * Updated documentation image by id
	 *
	 * @param resourceVersionId documentation id
	 * @param request           image resource data
	 *
	 * @return updated documentation image
	 */
	@PUT
	@Path("/documentation/image/{resourceVersionId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse updateDocumentationImage(@PathParam("resourceVersionId") Long resourceVersionId, ServiceRequest<ImageResourceVersion> request) {
		return OPFEngine.ContentService.updateDocumentationImage(resourceVersionId, request.getData());
	}

	/**
	 * Delete documentation by id
	 *
	 * @param resourceId documentation id
	 *
	 * @return deleted documentation
	 */
	@DELETE
	@Path("/documentation/{resourceId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse deleteDocumentation(@PathParam("resourceId") Long resourceId) {
		return OPFEngine.ContentService.deleteDocumentation(resourceId);
	}

	/**
	 * Read entity service links
	 *
	 * @param registryNodeId node id
	 *
	 * @return founded entity links
	 */
	@GET
	@Path("/documentation/links/{registryNodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<ActionServiceLink> readEntityServiceLinks(@PathParam("registryNodeId") Long registryNodeId) {
		return OPFEngine.ContentService.readEntityServiceLinks(registryNodeId);
	}

	/**
	 * Read examples by parent id
	 *
	 * @param registryNodeId parent id	 *
	 * @param country        country
	 *
	 * @return founded examples
	 */
	@GET
	@Path("/documentation/examples/{registryNodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<ActionServiceLink> readExamplesByParentId(
            @PathParam("registryNodeId") Long registryNodeId, @QueryParam("country") String country) {
		return OPFEngine.ContentService.readExamplesByParentId(registryNodeId, country);
	}

	/**
	 * Read descriptions by parent id
	 *
	 * @param nodeId  parent id
	 * @param country country
	 *
	 * @return founded descriptions
	 */
	@Deprecated
	@GET
	@Path("/documentation/descriptions/{nodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<AbstractResource> readDescriptionsByParentId(@PathParam("nodeId") Long nodeId, @QueryParam("country") String country) {
		return OPFEngine.ContentService.readDescriptionsByParentId(nodeId, country);
	}

	/**
	 * Read fields  by parent id
	 *
	 * @param nodeId  parent id
	 * @param country country
	 *
	 * @return founded fields
	 */
	@GET
	@Path("/documentation/fields/{nodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<FieldResource> readFields(@PathParam("nodeId") Long nodeId, @QueryParam("country") String country) {
		return OPFEngine.ContentService.readFields(nodeId, country);
	}

	/**
	 * Read Related Entities by parent id
	 *
	 * @param nodeId  parent id
	 * @param country country
	 *
	 * @return founded Related Entities
	 */
	@GET
	@Path("/documentation/entities/{nodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<EntityResourceRelationship> readRelatedEntities(@PathParam("nodeId") Long nodeId, @QueryParam("country") String country) {
		return OPFEngine.ContentService.readRelatedEntities(nodeId, country);
	}

	/**
	 * Read actions by parent id
	 *
	 * @param nodeId  parent id
	 * @param country country
	 *
	 * @return founded actions
	 */
	@GET
	@Path("/documentation/actions/{nodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<ActionResource> readActions(@PathParam("nodeId") Long nodeId, @QueryParam("country") String country) {
		return OPFEngine.ContentService.readActions(nodeId, country);
	}

	/**
	 * Read children by parent id
	 *
	 * @param nodeId parent id
	 *
	 * @return founded children
	 */
	@GET
	@Path("/documentation/children/{nodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Lookup> readChildren(@PathParam("nodeId") Long nodeId) {
		return OPFEngine.ContentService.readChildren(nodeId);
	}

	/**
	 * Read parents by children id
	 *
	 * @param nodeId children id
	 *
	 * @return founded parent
	 */
	@GET
	@Path("/documentation/parents/{nodeId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse<Lookup>readParents(@PathParam("nodeId") Long nodeId) {
		return OPFEngine.ContentService.readParents(nodeId);
	}

	/**
	 * Read documentation page
	 *
	 * @param country       country
	 * @param lookup        lookup
	 * @param maxImageWidth max image width
	 * @param token         token
	 *
	 * @return founded documentation
	 */
	@GET
	@Path("/documentation/html/{country}/{lookup}")
	@Produces(MediaType.TEXT_HTML)
    //todo: not valid in terms of OPF action abstraction
	public ServiceResponse<SimpleIdentifier<String>> getDocumentationHtmlSource(
			@PathParam("country") String country,
			@PathParam("lookup") String lookup,
			@QueryParam("maxImageWidth") Integer maxImageWidth,
			@CookieParam(OpenFlameSecurityConstants.AUTHENTICATION_TOKEN_ATTRIBUTE) String token) {
		return OPFEngine.ContentService.getDocumentationHtmlSource(lookup, country, maxImageWidth, token);
	}

	/**
	 * Generate  documentation example
	 * @return Generate status
	 */
	@PUT
	@Path("/documentation/generate/example/{lookup}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public ServiceResponse generateDocumentationExample(@PathParam("lookup") String lookup) {
		return OPFEngine.ContentService.generateDocumentationExample(lookup);
	}
}
