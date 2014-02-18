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

import net.firejack.platform.api.content.domain.*;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.SortOrder;
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
@WebService(endpointInterface = "net.firejack.platform.service.content.endpoint.IContentEndpoint")
public interface IContentEndpoint {
	/**
	 * Service retrieves the folder
	 *
	 * @param id - ID of the folder
	 *
	 * @return found folder
	 */
	@WebMethod
	ServiceResponse<Folder> readFolder(@WebParam(name = "id") Long id);

	/**
	 * Service retrieves the child folders by parent id.
	 *
	 * @param parentId - ID of the parent entity
	 *
	 * @return found list of folders
	 */
	@WebMethod
	ServiceResponse<Folder> readFoldersByParentId(@WebParam(name = "parentId") Long parentId);

	/**
	 * Service retrieves the child folders by parent lookup.
	 *
	 * @param lookup - lookup of the parent entity
	 *
	 * @return found list of folders
	 */
	@WebMethod
	ServiceResponse<Folder> readFoldersByLikeLookup(@WebParam(name = "lookup") String lookup);

	/**
	 * Service create a folder
	 *
	 * @param request - request with folder data
	 *
	 * @return created folder
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createFolder(@WebParam(name = "request") ServiceRequest<Folder> request);

	/**
	 * Service update the folder
	 *
	 * @param id - folder id which need to update
	 * @param request  - request with folder data
	 *
	 * @return updated folder
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateFolder(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<Folder> request);

	/**
	 * Service delete the folder by id
	 *
	 * @param folderId - folder id which need to delete
	 *
	 * @return deleted folder
	 */
	@WebMethod
	ServiceResponse<Folder> deleteFolder(@WebParam(name = "folderId") Long folderId);

	/**
	 * Service retrieves the collection
	 *
	 * @param id - ID of the collection
	 *
	 * @return found collection
	 */
	@WebMethod
	ServiceResponse<Collection> readCollection(@WebParam(name = "id") Long id);

	/**
	 * Service retrieves the child collections by parent id.
	 *
	 * @param parentId - ID of the parent entity
	 *
	 * @return found list of collections
	 */
	@WebMethod
	ServiceResponse<Collection> readCollectionsByParentId(@WebParam(name = "parentId") Long parentId);

	/**
	 * Service retrieves the child collections by like lookup.
	 *
	 * @param lookup for search
	 *
	 * @return list of collections
	 */
	@WebMethod
	ServiceResponse<Collection> readCollectionsByLikeLookup(@WebParam(name = "lookup") String lookup);

	/**
	 * Service create a collection
	 *
	 * @param request request with collection data
	 *
	 * @return created collection
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> createCollection(@WebParam(name = "request") ServiceRequest<Collection> request);

	/**
	 * Service update the collection
	 *
	 * @param id - collection id which need to update
	 * @param request      - request with collection data
	 *
	 * @return updated collection
	 */
	@WebMethod
	ServiceResponse<RegistryNodeTree> updateCollection(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<Collection> request);

	/**
	 * Service delete the collection by id
	 *
	 * @param id - collection id which need to delete
	 *
	 * @return deleted collection
	 */
	@WebMethod
	ServiceResponse deleteCollection(@WebParam(name = "id") Long id);

	@WebMethod(operationName = "exportCollectionArchiveFile")
	ServiceResponse<FileInfo> exportCollectionArchiveFileWS(@WebParam(name = "id") Long id);

	@WebMethod
	ServiceResponse<FileInfo> importCollectionArchiveFile(@WebParam(name = "byteArray") FileInfo byteArray);

	/**
	 * Swap position collection members
	 *
	 * @param id collection
	 * @param firstRefId     member reference id
	 * @param secondRefId     member reference id
	 *
	 * @return complete result
	 */
	@WebMethod
	ServiceResponse swapCollectionMemberships(
			@WebParam(name = "id") Long id,
			@WebParam(name = "firstRefId") Long firstRefId,
			@WebParam(name = "secondRefId") Long secondRefId);

	/**
	 * Service retrieves the list of all cultures.
	 *
	 * @return list of cultures
	 */
	@WebMethod
	ServiceResponse<Culture> readAllCultures();

	/**
	 * Service retrieves the list of available cultures.
	 *
	 * @param nodeId ID of entity for which need to get available cultures
	 *
	 * @return list of cultures
	 */
	@WebMethod
	ServiceResponse<Culture> readAvailableCultures(
			@WebParam(name = "nodeId") Long nodeId);

	/**
	 * Service retrieves the list of resources by parent id.
	 *
	 * @param registryNodeId ID of parent entity
	 * @param exceptIds      list of ids which need to exclude from results
	 * @param offset         start position
	 * @param limit          count of results
	 * @param sortColumn     sort column name
	 * @param sortDirection  sort direction ASC or DESC
	 *
	 * @return list of resources
	 */
	@WebMethod
	ServiceResponse<Resource> readResourcesByParentId(
			@WebParam(name = "registryNodeId") Long registryNodeId,
			@WebParam(name = "exceptIds") List<Long> exceptIds,
			@WebParam(name = "start") Integer offset,
			@WebParam(name = "limit") Integer limit,
			@WebParam(name = "sort") String sortColumn,
			@WebParam(name = "dir") SortOrder sortDirection);

	/**
	 * This method takes in a search term that will search across name to find any matches.
	 * In addition, the method should take in any name / value pairs for explicit filters for the user
	 * that correspond to other fields declared for the directory on the query string.
	 *
	 * @param registryNodeId ID of parent entity
	 * @param term           it is a search term
	 *
	 * @return list of found resources
	 */
	@WebMethod
	ServiceResponse<Resource> searchResourceList(
			@WebParam(name = "registryNodeId") Long registryNodeId,
			@WebParam(name = "term") String term);

	@WebMethod
	ServiceResponse<FileInfo> uploadImage(
            @WebParam(name = "resourceType") String resourceType,
            @WebParam(name = "ttl") String customTTL, @WebParam(name = "file") FileInfo data);

	@WebMethod(operationName = "getTmpUploadedImage")
	ServiceResponse<FileInfo> getTmpUploadedImageWS(@WebParam(name = "temporaryFilename") String temporaryFilename);

	/**
	 * Service retrieves text resource by ID for last version and american culture
	 *
	 * @param resourceId ID of text resource
	 *
	 * @return found text resource
	 */
	@WebMethod
	ServiceResponse<TextResource> readTextResourceById(@WebParam(name = "resourceId") Long resourceId);

	/**
	 * Service retrieves text resource by ID
	 *
	 * @param resourceId ID of text resource
	 * @param culture    is one of value from Cultures enum
	 * @param version    version of resources
	 *
	 * @return found text resource
	 */
	@WebMethod
	ServiceResponse<TextResource> readTextResourceByIdCultureAndVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "culture") String culture,
			@WebParam(name = "version") Integer version);

	/**
	 * Service retrieves text resource by lookup
	 *
	 * @param lookup lookup of text resource
	 *
	 * @return found text resource
	 */
	@WebMethod
	ServiceResponse<TextResource> readTextResourceByLookup(@WebParam(name = "lookup") String lookup);

	/**
	 * Service allows to create a new text resource
	 *
	 * @param request request with text resource data
	 *
	 * @return created text resource
	 */
	@WebMethod
	ServiceResponse<TextResource> createTextResource(@WebParam(name = "request") ServiceRequest<TextResource> request);

	/**
	 * Service allows to create a copy text resource versions from the last version but increase version number
	 *
	 * @param resourceId ID of text resource
	 *
	 * @return text resource with new text resource version
	 */
	@WebMethod
	ServiceResponse<TextResource> createNewTextResourceVersionForLastVersion(@WebParam(name = "resourceId") Long resourceId);

	/**
	 * Service allows to create copy text resource versions from defined version but increase version number
	 *
	 * @param resourceId ID of text resource
	 * @param version    version number from which need to create new ones
	 *
	 * @return text resource with new text resource version
	 */
	@WebMethod
	ServiceResponse<TextResource> createNewTextResourceVersionForDefinedVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "version") Integer version);

	/**
	 * Service updates text resource
	 *
	 * @param id ID of text resource
	 * @param request    text resource which need to update
	 *
	 * @return updated text resource
	 */
	@WebMethod
	ServiceResponse<TextResource> updateTextResource(
			@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<TextResource> request);

	/**
	 * Service updates text resource version
	 *
	 * @param id ID of text resource version
	 * @param request           text resource version
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse<TextResourceVersion> updateTextResourceVersion(
			@WebParam(name = "id") Long id, @WebParam(name = "request") ServiceRequest<TextResourceVersion> request);

	/**
	 * Service deletes text resource with all versions
	 *
	 * @param id ID of text resource
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse deleteTextResource(@WebParam(name = "id") Long id);

	/**
	 * Service deletes text resource version
	 *
	 * @param resourceId ID of text resource
	 * @param culture    is one of value from Cultures enum
	 * @param version    version of resources
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse deleteTextResourceVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "version") Integer version,
			@WebParam(name = "culture") String culture);

	/**
	 * Service retrieves html resource by ID for last version and american culture
	 *
	 * @param id ID of html resource
	 *
	 * @return found html resource
	 */
	@WebMethod
	ServiceResponse<HtmlResource> readHtmlResourceById(@WebParam(name = "id") Long id);

	/**
	 * Service retrieves html resource by ID
	 *
	 * @param resourceId ID of html resource
	 * @param culture    is one of value from Cultures enum
	 * @param version    version of resources
	 *
	 * @return found html resource
	 */
	@WebMethod
	ServiceResponse<HtmlResource> readHtmlResourceByIdCultureAndVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "culture") String culture,
			@WebParam(name = "version") Integer version);

	/**
	 * Service retrieves html resource by lookup
	 *
	 * @param lookup lookup of html resource
	 *
	 * @return found html resource
	 */
	@WebMethod
	ServiceResponse<HtmlResource> readHtmlResourceByLookup(@WebParam(name = "lookup") String lookup);

	/**
	 * Service allows to create a new html resource
	 *
	 * @param request request with html resource data
	 *
	 * @return created html resource
	 */
	@WebMethod
	ServiceResponse<HtmlResource> createHtmlResource(@WebParam(name = "request") ServiceRequest<HtmlResource> request);

	/**
	 * Service allows to create a copy html resource versions from the last version but increase version number
	 *
	 * @param resourceId ID of html resource
	 *
	 * @return html resource with new html resource version
	 */
	@WebMethod
	ServiceResponse<HtmlResource> createNewHtmlResourceVersionForLastVersion(@WebParam(name = "resourceId") Long resourceId);

	/**
	 * Service allows to create copy html resource versions from defined version but increase version number
	 *
	 * @param resourceId ID of html resource
	 * @param version    version number from which need to create new ones
	 *
	 * @return html resource with new html resource version
	 */
	@WebMethod
	ServiceResponse<HtmlResource> createNewHtmlResourceVersionForDefinedVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "version") Integer version);

	/**
	 * Service updates html resource
	 *
	 * @param id ID of html resource
	 * @param request    html resource which need to update
	 *
	 * @return updated html resource
	 */
	@WebMethod
	ServiceResponse<HtmlResource> updateHtmlResource(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<HtmlResource> request);

	/**
	 * Service updates html resource version
	 *
	 * @param resourceVersionId ID of html resource version
	 * @param request           html resource version
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse updateHtmlResourceVersion(
			@WebParam(name = "resourceVersionId") Long resourceVersionId,
			@WebParam(name = "request") ServiceRequest<HtmlResourceVersion> request);

	/**
	 * Service deletes html resource with all versions
	 *
	 * @param id ID of html resource
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse deleteHtmlResource(@WebParam(name = "id") Long id);

	/**
	 * Service deletes html resource version
	 *
	 * @param resourceId ID of html resource
	 * @param culture    is one of value from Cultures enum
	 * @param version    version of resources
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse deleteHtmlResourceVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "version") Integer version,
			@WebParam(name = "culture") String culture);

	/**
	 * Service retrieves image resource by ID for last version and american culture
	 *
	 * @param id ID of image resource
	 *
	 * @return found image resource
	 */
	@WebMethod
	ServiceResponse<ImageResource> readImageResourceById(@WebParam(name = "id") Long id);

	/**
	 * Service retrieves image resource by ID
	 *
	 * @param resourceId ID of image resource
	 * @param culture    is one of value from Cultures enum
	 * @param version    version of resources
	 *
	 * @return found image resource
	 */
	@WebMethod
	ServiceResponse<ImageResource> readImageResourceByIdCultureAndVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "culture") String culture,
			@WebParam(name = "version") Integer version);

	/**
	 * Service retrieves image resource by lookup
	 *
	 * @param lookup lookup of image resource
	 *
	 * @return found image resource
	 */
	@WebMethod
	ServiceResponse<ImageResource> readImageResourceByLookup(@WebParam(name = "lookup") String lookup);

	/**
	 * Service allows to create a new image resource
	 *
	 * @param request request with image resource data
	 *
	 * @return created image resource
	 */
	@WebMethod
	ServiceResponse<ImageResource> createImageResource(@WebParam(name = "request") ServiceRequest<ImageResource> request);

	@WebMethod(operationName = "getImageResourceVersionFile")
	ServiceResponse<FileInfo> getImageResourceVersionFileWS(@WebParam(name = "resourceId") Long resourceId, @WebParam(name = "imageFilename") String imageFilename);

	/**
	 * Service allows to create a copy image resource versions from the last version but increase version number
	 *
	 * @param resourceId ID of image resource
	 *
	 * @return image resource with new image resource version
	 */
	@WebMethod
	ServiceResponse<ImageResource> createNewImageResourceVersionForLastVersion(@WebParam(name = "resourceId") Long resourceId);

	/**
	 * Service allows to create copy image resource versions from defined version but increase version number
	 *
	 * @param resourceId ID of image resource
	 * @param version    version number from which need to create new ones
	 *
	 * @return image resource with new image resource version
	 */
	@WebMethod
	ServiceResponse<ImageResource> createNewImageResourceVersionForDefinedVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "version") Integer version);

	/**
	 * Service updates image resource
	 *
	 * @param id ID of image resource
	 * @param request    image resource which need to update
	 *
	 * @return updated image resource
	 */
	@WebMethod
	ServiceResponse<ImageResource> updateImageResource(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<ImageResource> request);

    /**
     * Service creates image resource version
     *
     * @param resourceLookup ID of image resource version
     * @param request image resource version
     * @return message
     */
    ServiceResponse<ImageResourceVersion> createImageResourceVersionByLookup(
             @WebParam(name = "resourceLookup") String resourceLookup,
             @WebParam(name = "request") ServiceRequest<ImageResourceVersion> request);

    /**
     * Service creates image resource version
     *
     * @param resourcePath ID of image resource version
     * @param request image resource version
     * @return message
     */
    ServiceResponse<ImageResourceVersion> createImageResourceVersionByPath(
             @WebParam(name = "resourcePath") String resourcePath,
             @WebParam(name = "request") ServiceRequest<ImageResourceVersion> request);

	/**
	 * Service updates image resource version
	 *
	 * @param resourceVersionId ID of image resource version
	 * @param request           image resource version
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse updateImageResourceVersion(
			@WebParam(name = "resourceVersionId") Long resourceVersionId,
			@WebParam(name = "request") ServiceRequest<ImageResourceVersion> request);

    /**
     * Service updates image resource version
     *
     * @param resourceLookup lookup of image resource version
     * @param request image resource version
     * @return message
     */
    @WebMethod
    ServiceResponse updateImageResourceVersionByLookup(
             @WebParam(name = "resourceLookup") String resourceLookup,
             @WebParam(name = "request") ServiceRequest<ImageResourceVersion> request);

	/**
	 * Service deletes image resource with all versions
	 *
	 * @param id ID of image resource
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse deleteImageResource(@WebParam(name = "id") Long id);

    /**
     * Service deletes image resource with all versions
     *
     * @param lookup lookup of image resource
     * @return message
     */
    @WebMethod
    ServiceResponse deleteImageResourceByLookup(@WebParam(name = "lookup") String lookup);

	/**
	 * Service deletes image resource version
	 *
	 * @param resourceId ID of image resource
	 * @param culture    is one of value from Cultures enum
	 * @param version    version of resources
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse deleteImageResourceVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "version") Integer version,
			@WebParam(name = "culture") String culture);

	/**
	 * Service retrieves audio resource by ID for last version and american culture
	 *
	 * @param resourceId ID of audio resource
	 *
	 * @return found audio resource
	 */
	@WebMethod
	ServiceResponse<AudioResource> readAudioResourceById(
			@WebParam(name = "resourceId") Long resourceId);

	/**
	 * Service retrieves audio resource by ID
	 *
	 * @param resourceId ID of audio resource
	 * @param culture    is one of value from Cultures enum
	 * @param version    version of resources
	 *
	 * @return found audio resource
	 */
	@WebMethod
	ServiceResponse<AudioResource> readAudioResourceByIdCultureAndVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "culture") String culture,
			@WebParam(name = "version") Integer version);

	/**
	 * Service retrieves audio resource by lookup
	 *
	 * @param lookup lookup of audio resource
	 *
	 * @return found audio resource
	 */
	@WebMethod
	ServiceResponse<AudioResource> readAudioResourceByLookup(@WebParam(name = "lookup") String lookup);

	/**
	 * Service allows to create a new audio resource
	 *
	 * @param request request with audio resource data
	 *
	 * @return created audio resource
	 */
	@WebMethod
	ServiceResponse<AudioResource> createAudioResource(@WebParam(name = "request") ServiceRequest<AudioResource> request);

	@WebMethod(operationName = "getAudioResourceVersionFile")
	ServiceResponse<FileInfo> getAudioResourceVersionFileWS(@WebParam(name = "resourceId") Long resourceId, @WebParam(name = "audioFilename") String audioFilename);

	/**
	 * Service allows to create a copy audio resource versions from the last version but increase version number
	 *
	 * @param resourceId ID of audio resource
	 *
	 * @return audio resource with new audio resource version
	 */
	@WebMethod
	ServiceResponse<AudioResource> createNewAudioResourceVersionForLastVersion(@WebParam(name = "resourceId") Long resourceId);

	/**
	 * Service allows to create copy audio resource versions from defined version but increase version number
	 *
	 * @param resourceId ID of audio resource
	 * @param version    version number from which need to create new ones
	 *
	 * @return audio resource with new audio resource version
	 */
	@WebMethod
	ServiceResponse<AudioResource> createNewAudioResourceVersionForDefinedVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "version") Integer version);

	/**
	 * Service updates audio resource
	 *
	 * @param id ID of audio resource
	 * @param request    audio resource which need to update
	 *
	 * @return updated audio resource
	 */
	@WebMethod
	ServiceResponse<AudioResource> updateAudioResource(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<AudioResource> request);

	/**
	 * Service updates audio resource version
	 *
	 * @param id ID of audio resource version
	 * @param request           audio resource version
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse updateAudioResourceVersion(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<AudioResourceVersion> request);

	/**
	 * Service deletes audio resource with all versions
	 *
	 * @param id ID of audio resource
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse deleteAudioResource(@WebParam(name = "id") Long id);

	/**
	 * Service deletes audio resource version
	 *
	 * @param resourceId ID of audio resource
	 * @param culture    is one of value from Cultures enum
	 * @param version    version of resources
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse deleteAudioResourceVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "version") Integer version,
			@WebParam(name = "culture") String culture);

	/**
	 * Service retrieves video resource by ID for last version and american culture
	 *
	 * @param id ID of video resource
	 *
	 * @return found video resource
	 */
	@WebMethod
	ServiceResponse<VideoResource> readVideoResourceById(@WebParam(name = "id") Long id);

	/**
	 * Service retrieves video resource by ID
	 *
	 * @param resourceId ID of video resource
	 * @param culture    is one of value from Cultures enum
	 * @param version    version of resources
	 *
	 * @return found video resource
	 */
	@WebMethod
	ServiceResponse<VideoResource> readVideoResourceByIdCultureAndVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "culture") String culture,
			@WebParam(name = "version") Integer version);

	/**
	 * Service retrieves video resource by lookup
	 *
	 * @param lookup lookup of video resource
	 *
	 * @return found video resource
	 */
	@WebMethod
	ServiceResponse<VideoResource> readVideoResourceByLookup(@WebParam(name = "lookup") String lookup);

	@WebMethod(operationName = "getVideoResourceVersionFile")
	ServiceResponse<FileInfo> getVideoResourceVersionFileWS(@WebParam(name = "resourceId") Long resourceId, @WebParam(name = "videoFilename") String videoFilename);

	/**
	 * Service allows to create a new video resource
	 *
	 * @param request request with video resource data
	 *
	 * @return created video resource
	 */
	@WebMethod
	ServiceResponse<VideoResource> createVideoResource(@WebParam(name = "request") ServiceRequest<VideoResource> request);

	/**
	 * Service allows to create a copy video resource versions from the last version but increase version number
	 *
	 * @param resourceId ID of video resource
	 *
	 * @return video resource with new video resource version
	 */
	@WebMethod
	ServiceResponse<VideoResource> createNewVideoResourceVersionForLastVersion(@WebParam(name = "resourceId") Long resourceId);

	/**
	 * Service allows to create copy video resource versions from defined version but increase version number
	 *
	 * @param resourceId ID of video resource
	 * @param version    version number from which need to create new ones
	 *
	 * @return video resource with new video resource version
	 */
	@WebMethod
	ServiceResponse<VideoResource> createNewVideoResourceVersionForDefinedVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "version") Integer version);

	/**
	 * Service updates video resource
	 *
	 * @param id ID of video resource
	 * @param request    video resource which need to update
	 *
	 * @return updated video resource
	 */
	@WebMethod
	ServiceResponse<VideoResource> updateVideoResource(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<VideoResource> request);

	/**
	 * Service updates video resource version
	 *
	 * @param id ID of video resource version
	 * @param request           video resource version
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse updateVideoResourceVersion(
			@WebParam(name = "id") Long id,
			@WebParam(name = "request") ServiceRequest<VideoResourceVersion> request);

	/**
	 * Service deletes video resource with all versions
	 *
	 * @param id ID of video resource
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse deleteVideoResource(@WebParam(name = "id") Long id);

	/**
	 * Service deletes video resource version
	 *
	 * @param resourceId ID of video resource
	 * @param culture    is one of value from Cultures enum
	 * @param version    version of resources
	 *
	 * @return message
	 */
	@WebMethod
	ServiceResponse deleteVideoResourceVersion(
			@WebParam(name = "resourceId") Long resourceId,
			@WebParam(name = "version") Integer version,
			@WebParam(name = "culture") String culture);

    /**
     * Service retrieves document resource by ID for last version and american culture
     *
     * @param id ID of document resource
     *
     * @return found document resource
     */
    @WebMethod
    ServiceResponse<DocumentResource> readDocumentResourceById(@WebParam(name = "id") Long id);

    /**
     * Service retrieves document resource by ID
     *
     * @param resourceId ID of document resource
     * @param culture    is one of value from Cultures enum
     * @param version    version of resources
     *
     * @return found document resource
     */
    @WebMethod
    ServiceResponse<DocumentResource> readDocumentResourceByIdCultureAndVersion(
            @WebParam(name = "resourceId") Long resourceId,
            @WebParam(name = "culture") String culture,
            @WebParam(name = "version") Integer version);

    /**
     * Service retrieves document resource by lookup
     *
     * @param lookup lookup of document resource
     *
     * @return found document resource
     */
    @WebMethod
    ServiceResponse<DocumentResource> readDocumentResourceByLookup(@WebParam(name = "lookup") String lookup);

    @WebMethod(operationName = "getDocumentResourceVersionFile")
    ServiceResponse<FileInfo> getDocumentResourceVersionFileWS(@WebParam(name = "resourceId") Long resourceId, @WebParam(name = "documentFilename") String documentFilename);

    /**
     * Service allows to create a new document resource
     *
     * @param request request with document resource data
     *
     * @return created document resource
     */
    @WebMethod
    ServiceResponse<DocumentResource> createDocumentResource(@WebParam(name = "request") ServiceRequest<DocumentResource> request);

    /**
     * Service allows to create a copy document resource versions from the last version but increase version number
     *
     * @param resourceId ID of document resource
     *
     * @return document resource with new document resource version
     */
    @WebMethod
    ServiceResponse<DocumentResource> createNewDocumentResourceVersionForLastVersion(@WebParam(name = "resourceId") Long resourceId);

    /**
     * Service allows to create copy document resource versions from defined version but increase version number
     *
     * @param resourceId ID of document resource
     * @param version    version number from which need to create new ones
     *
     * @return document resource with new document resource version
     */
    @WebMethod
    ServiceResponse<DocumentResource> createNewDocumentResourceVersionForDefinedVersion(
            @WebParam(name = "resourceId") Long resourceId,
            @WebParam(name = "version") Integer version);

    /**
     * Service updates document resource
     *
     * @param id ID of document resource
     * @param request    document resource which need to update
     *
     * @return updated document resource
     */
    @WebMethod
    ServiceResponse<DocumentResource> updateDocumentResource(
            @WebParam(name = "id") Long id,
            @WebParam(name = "request") ServiceRequest<DocumentResource> request);

    /**
     * Service updates document resource version
     *
     * @param id ID of document resource version
     * @param request           document resource version
     *
     * @return message
     */
    @WebMethod
    ServiceResponse updateDocumentResourceVersion(
            @WebParam(name = "id") Long id,
            @WebParam(name = "request") ServiceRequest<DocumentResourceVersion> request);

    /**
     * Service deletes document resource with all versions
     *
     * @param id ID of document resource
     *
     * @return message
     */
    @WebMethod
    ServiceResponse deleteDocumentResource(@WebParam(name = "id") Long id);

    /**
     * Service deletes document resource version
     *
     * @param resourceId ID of document resource
     * @param culture    is one of value from Cultures enum
     * @param version    version of resources
     *
     * @return message
     */
    @WebMethod
    ServiceResponse deleteDocumentResourceVersion(
            @WebParam(name = "resourceId") Long resourceId,
            @WebParam(name = "version") Integer version,
            @WebParam(name = "culture") String culture);

    /**
     * Service retrieves file resource by ID for last version and american culture
     *
     * @param id ID of file resource
     *
     * @return found file resource
     */
    @WebMethod
    ServiceResponse<FileResource> readFileResourceById(@WebParam(name = "id") Long id);

    /**
     * Service retrieves file resource by ID
     *
     * @param resourceId ID of file resource
     * @param culture    is one of value from Cultures enum
     * @param version    version of resources
     *
     * @return found file resource
     */
    @WebMethod
    ServiceResponse<FileResource> readFileResourceByIdCultureAndVersion(
            @WebParam(name = "resourceId") Long resourceId,
            @WebParam(name = "culture") String culture,
            @WebParam(name = "version") Integer version);

    /**
     * Service retrieves file resource by lookup
     *
     * @param lookup lookup of file resource
     *
     * @return found file resource
     */
    @WebMethod
    ServiceResponse<FileResource> readFileResourceByLookup(@WebParam(name = "lookup") String lookup);

    @WebMethod(operationName = "getFileResourceVersionFile")
    ServiceResponse<FileInfo> getFileResourceVersionFileWS(@WebParam(name = "resourceId") Long resourceId, @WebParam(name = "fileFilename") String fileFilename);

    /**
     * Service allows to create a new file resource
     *
     * @param request request with file resource data
     *
     * @return created file resource
     */
    @WebMethod
    ServiceResponse<FileResource> createFileResource(@WebParam(name = "request") ServiceRequest<FileResource> request);

    /**
     * Service allows to create a copy file resource versions from the last version but increase version number
     *
     * @param resourceId ID of file resource
     *
     * @return file resource with new file resource version
     */
    @WebMethod
    ServiceResponse<FileResource> createNewFileResourceVersionForLastVersion(@WebParam(name = "resourceId") Long resourceId);

    /**
     * Service allows to create copy file resource versions from defined version but increase version number
     *
     * @param resourceId ID of file resource
     * @param version    version number from which need to create new ones
     *
     * @return file resource with new file resource version
     */
    @WebMethod
    ServiceResponse<FileResource> createNewFileResourceVersionForDefinedVersion(
            @WebParam(name = "resourceId") Long resourceId,
            @WebParam(name = "version") Integer version);

    /**
     * Service updates file resource
     *
     * @param id ID of file resource
     * @param request    file resource which need to update
     *
     * @return updated file resource
     */
    @WebMethod
    ServiceResponse<FileResource> updateFileResource(
            @WebParam(name = "id") Long id,
            @WebParam(name = "request") ServiceRequest<FileResource> request);

    /**
     * Service updates file resource version
     *
     * @param id ID of file resource version
     * @param request           file resource version
     *
     * @return message
     */
    @WebMethod
    ServiceResponse updateFileResourceVersion(
            @WebParam(name = "id") Long id,
            @WebParam(name = "request") ServiceRequest<FileResourceVersion> request);

    /**
     * Service deletes file resource with all versions
     *
     * @param id ID of file resource
     *
     * @return message
     */
    @WebMethod
    ServiceResponse deleteFileResource(@WebParam(name = "id") Long id);

    /**
     * Service deletes file resource version
     *
     * @param resourceId ID of file resource
     * @param culture    is one of value from Cultures enum
     * @param version    version of resources
     *
     * @return message
     */
    @WebMethod
    ServiceResponse deleteFileResourceVersion(
            @WebParam(name = "resourceId") Long resourceId,
            @WebParam(name = "version") Integer version,
            @WebParam(name = "culture") String culture);

	/**
	 * Create new documentation
	 *
	 * @param request documentation data
	 *
	 * @return created document
	 */
	@WebMethod
	public ServiceResponse<ResourceContent> createDocumentation(@WebParam(name = "request") ServiceRequest<ResourceContent> request);

	/**
	 * Update documentation by id
	 *
	 * @param resourceVersionId documentation id
	 * @param request           documentation data
	 *
	 * @return updated documentation
	 */
	@WebMethod
	public ServiceResponse<ResourceContent> updateDocumentation(@WebParam(name = "resourceVersionId") Long resourceVersionId, @WebParam(name = "request") ServiceRequest<ResourceContent> request);

	/**
	 * Updated documentation image by id
	 *
	 * @param resourceVersionId documentation id
	 * @param request           image resource data
	 *
	 * @return updated documentation image
	 */
	@WebMethod
	public ServiceResponse updateDocumentationImage(@WebParam(name = "resourceVersionId") Long resourceVersionId, @WebParam(name = "request") ServiceRequest<ImageResourceVersion> request);

	/**
	 * Delete documentation by id
	 *
	 * @param resourceId documentation id
	 *
	 * @return deleted documentation
	 */
	@WebMethod
	public ServiceResponse deleteDocumentation(@WebParam(name = "resourceId") Long resourceId);

	/**
	 * Read entity service links
	 *
	 * @param registryNodeId node id
	 *
	 * @return founded entity links
	 */
	@WebMethod
	public ServiceResponse<ActionServiceLink> readEntityServiceLinks(@WebParam(name = "registryNodeId") Long registryNodeId);

	/**
	 * Read examples by parent id
	 *
	 * @param registryNodeId parent id	 *
	 * @param country        country
	 *
	 * @return founded examples
	 */
	@WebMethod
	public ServiceResponse<ActionServiceLink> readExamplesByParentId(@WebParam(name = "registryNodeId") Long registryNodeId, @WebParam(name = "country") String country);

	/**
	 * Read descriptions by parent id
	 *
	 * @param nodeId  parent id
	 * @param country country
	 *
	 * @return founded descriptions
	 */
	@Deprecated
	@WebMethod
	public ServiceResponse<AbstractResource> readDescriptionsByParentId(@WebParam(name = "nodeId") Long nodeId, @WebParam(name = "country") String country);

	/**
	 * Read fields  by parent id
	 *
	 * @param nodeId  parent id
	 * @param country country
	 *
	 * @return founded fields
	 */
	@WebMethod
	public ServiceResponse<FieldResource> readFields(@WebParam(name = "nodeId") Long nodeId, @WebParam(name = "country") String country);

	/**
	 * Read Related Entities by parent id
	 *
	 * @param nodeId  parent id
	 * @param country country
	 *
	 * @return founded Related Entities
	 */
	@WebMethod
	public ServiceResponse<EntityResourceRelationship> readRelatedEntities(@WebParam(name = "nodeId") Long nodeId, @WebParam(name = "country") String country);

	/**
	 * Read actions by parent id
	 *
	 * @param nodeId  parent id
	 * @param country country
	 *
	 * @return founded actions
	 */
	@WebMethod
	public ServiceResponse<ActionResource> readActions(@WebParam(name = "nodeId") Long nodeId, @WebParam(name = "country") String country);

	/**
	 * Read children by parent id
	 *
	 * @param nodeId parent id
	 *
	 * @return founded children
	 */
	@WebMethod
	public ServiceResponse<Lookup> readChildren(@WebParam(name = "nodeId") Long nodeId);

	/**
	 * Read parents by children id
	 *
	 * @param nodeId children id
	 *
	 * @return founded parent
	 */
	@WebMethod
	public ServiceResponse<Lookup> readParents(@WebParam(name = "nodeId") Long nodeId);

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
	@WebMethod
	public ServiceResponse getDocumentationHtmlSource(
			@WebParam(name = "country") String country,
			@WebParam(name = "lookup") String lookup,
			@WebParam(name = "maxImageWidth") Integer maxImageWidth,
			@WebParam(name = "token") String token);
}
