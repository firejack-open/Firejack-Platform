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

package net.firejack.platform.api.content;

import net.firejack.platform.api.content.domain.*;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.ListLookup;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;

import java.io.InputStream;
import java.util.List;

public interface IContentService {

    /**
     * Service retrieves the folder
     *
     * @param folderId - ID of the folder
     * @return response with found folder
     */
    ServiceResponse<Folder> readFolder(Long folderId);

    /**
     * Service retrieves the child folders by parent id.
     *
     * @param parentId - ID of the parent entity
     * @return found list of folders
     */
    ServiceResponse<Folder> readFoldersByParentId(Long parentId);

    /**
     * Service retrieves the child folders by parent lookup.
     *
     * @param lookup - lookup of the parent entity
     * @return found list of folders
     */
    ServiceResponse<Folder> readFoldersByLikeLookup(String lookup);

    /**
     * Service create a folder
     *
     * @param folder - folder data
     * @return created folder
     */
    ServiceResponse<RegistryNodeTree> createFolder(Folder folder);

    /**
     * Service update the folder
     *
     * @param folder - folder which need to update
     * @return updated folder
     */
    ServiceResponse<RegistryNodeTree> updateFolder(Folder folder);

    /**
     * Service delete the folder by id
     *
     * @param folderId - folder id which need to delete
     * @return deleted folder
     */
    ServiceResponse deleteFolder(Long folderId);

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//     COLLECTION SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves the collection
     *
     * @param collectionId - ID of the collection
     * @return found collection
     */
    ServiceResponse<Collection> readCollection(Long collectionId);

    /**
     * Service retrieves the child collections by parent id.
     *
     * @param parentId - ID of the parent entity
     * @return found list of collections
     */
    ServiceResponse<Collection> readCollectionsByParentId(Long parentId);

    /**
     * Service retrieves the child collections by like lookup.
     *
     * @param lookup for search
     * @return list of collections
     */
    ServiceResponse<Collection> readCollectionsByLikeLookup(String lookup);

    /**
     * Service create a collection
     *
     * @param collection collection data for create
     * @return created collection
     */
    ServiceResponse<RegistryNodeTree> createCollection(Collection collection);

    /**
     * Service update the collection
     *
     * @param collection - collection id which need to update
     * @return updated collection
     */
    ServiceResponse<RegistryNodeTree> updateCollection(Collection collection);

    /**
     * Service delete the collection by id
     *
     * @param collectionId - collection id which need to delete
     * @return deleted collection
     */
    ServiceResponse deleteCollection(Long collectionId);

    /**
     * Service export the collection by id with all sub collections, all resources and folders.
     *
     * @param collectionId collection ID which need to export to file
     * @return response with exported collection stream
     */
    ServiceResponse<FileInfo> exportCollectionArchiveFile(Long collectionId);

    /**
     * Service import the content archive file which contains collections, folders and resources.
     *
     * @param inputStream of imported content archive file
     * @return results about imported process
     */
    ServiceResponse importCollectionArchiveFile(InputStream inputStream);

    /**
	 * Swap position collection members
	 *
	 * @param collectionId collection
	 * @param oneRefId member reference id
	 * @param twoRefId member reference id
	 * @return complete result
	 */
    ServiceResponse swapCollectionMemberships(Long collectionId, Long oneRefId, Long twoRefId);

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves the list of all cultures.
     *
     * @return list of cultures
     */
    ServiceResponse<Culture> readAllCultures();

    /**
     * Service retrieves the list of available cultures.
     *
     * @param entityId ID of entity for which need to get available cultures
     * @return list of cultures
     */
    ServiceResponse<Culture> readAvailableCultures(Long entityId);

    /**
     * Service retrieves the list of resources by parent id.
     *
     * @param registryNodeId ID of parent entity
     * @param exceptIds list of ids which need to exclude from results
     * @param paging offset - start position, limit - count of results, sortColumn - sort column name, sortDirection - sort direction ASC or DESC
     * @return list of resources
     */
    ServiceResponse<Resource> readResourcesByParentId(Long registryNodeId, List<Long> exceptIds, Paging paging);

    /**
     * This method takes in a search term that will search across name to find any matches.
     * In addition, the method should take in any name / value pairs for explicit filters for the user
     * that correspond to other fields declared for the directory on the query string.
     *
     * @param registryNodeId ID of parent entity
     * @param term it is a search term
     * @return list of found resources
     */
    ServiceResponse<Resource> searchResourcesByParentIdAndTerm(Long registryNodeId, String term);

    /**
     * Service method allows to upload resource file like image, audio and video
     *
     *
     * @param resourceType one of the list from ResourceType enum
     * @param customTTL custom time to live value
     * @param inputStream uploaded file stream
     * @param originalFilename original filename of uploaded file   @return response FileInfo with temporary uploaded file name and additional info about file, like for instance for image: width, height
     * @return returns uploaded file information
     */
    ServiceResponse<FileInfo> uploadResourceFile(String resourceType, String customTTL, InputStream inputStream, String originalFilename);

    /**
     * Service returns temporary uploaded file by temporary filename
     * @param temporaryFilename temporary filename which
     * @return uploaded file input stream
     */
    ServiceResponse<FileInfo> getTemporaryUploadedFile(String temporaryFilename);

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    TEXT RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves text resource by ID for last version and american culture
     *
     * @param textResourceId ID of text resource
     * @return found text resource
     */
    ServiceResponse<TextResource> readTextResourceById(Long textResourceId);

    /**
     * Service retrieves text resource by ID
     *
     * @param textResourceId ID of text resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found text resource
     */
    ServiceResponse<TextResource> readTextResourceByIdCultureAndVersion(Long textResourceId, String culture, Integer version);

    /**
     * Service retrieves text resource by lookup
     *
     * @param lookup lookup of text resource
     * @return found text resource
     */
    ServiceResponse<TextResource> readTextResourceByLookup(String lookup);

    /**
     * Service allows to create a new text resource
     *
     * @param textResource text resource for save
     * @return created text resource
     */
    ServiceResponse<TextResource> createTextResource(TextResource textResource);

    /**
     * Service allows to create copy text resource versions from the last version but increase version number
     *
     * @param textResourceId ID of text resource
     * @return text resource with new text resource version
     */
    ServiceResponse<TextResource> createNewTextResourceVersions(Long textResourceId);


    /**
     * Service allows to create copy text resource versions from defined version but increase version number
     *
     * @param textResourceId ID of text resource
     * @param version version number from which need to create new ones
     * @return text resource with new text resource version
     */
    ServiceResponse<TextResource> createNewTextResourceVersions(Long textResourceId, Integer version);

    /**
     * Service updates text resource
     *
     * @param textResource text resource which need to update
     * @return updated text resource
     */
    ServiceResponse<TextResource> updateTextResource(TextResource textResource);

    /**
     * Service creates text resource version
     *
     * @param resourceLookup text resource lookup
     * @param textResourceVersion text resource version
     * @return message
     */
    ServiceResponse<TextResourceVersion> createTextResourceVersion(String resourceLookup, TextResourceVersion textResourceVersion);

    /**
     * Service updates text resource version
     *
     * @param textResourceVersion text resource version
     * @return message
     */
    ServiceResponse<TextResourceVersion> updateTextResourceVersion(TextResourceVersion textResourceVersion);

    /**
     * Service deletes text resource with all versions
     *
     * @param textResourceId ID of text resource
     * @return message
     */
    ServiceResponse deleteTextResource(Long textResourceId);

    /**
     * Service deletes text resource version
     *
     * @param textResourceId ID of text resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    ServiceResponse deleteTextResourceVersion(Long textResourceId, String culture, Integer version);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    HTML RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves html resource by ID for last version and american culture
     *
     * @param htmlResourceId ID of html resource
     * @return found html resource
     */
    ServiceResponse<HtmlResource> readHtmlResourceById(Long htmlResourceId);

    /**
     * Service retrieves html resource by ID
     *
     * @param htmlResourceId ID of html resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found html resource
     */
    ServiceResponse<HtmlResource> readHtmlResourceByIdCultureAndVersion(Long htmlResourceId, String culture, Integer version);

    /**
     * Service retrieves html resource by lookup
     *
     * @param lookup lookup of html resource
     * @return found html resource
     */
    ServiceResponse<HtmlResource> readHtmlResourceByLookup(String lookup);

    /**
     * Service allows to create a new html resource
     *
     * @param htmlResource html resource for save
     * @return created html resource
     */
    ServiceResponse<HtmlResource> createHtmlResource(HtmlResource htmlResource);

    /**
     * Service allows to create copy html resource versions from the last version but increase version number
     *
     * @param htmlResourceId ID of html resource
     * @return html resource with new html resource version
     */
    ServiceResponse<HtmlResource> createNewHtmlResourceVersions(Long htmlResourceId);


    /**
     * Service allows to create copy html resource versions from defined version but increase version number
     *
     * @param htmlResourceId ID of html resource
     * @param version version number from which need to create new ones
     * @return html resource with new html resource version
     */
    ServiceResponse<HtmlResource> createNewHtmlResourceVersions(Long htmlResourceId, Integer version);

    /**
     * Service updates html resource
     *
     * @param htmlResource html resource which need to update
     * @return updated html resource
     */
    ServiceResponse<HtmlResource> updateHtmlResource(HtmlResource htmlResource);

    /**
     * Service updates html resource version
     *
     * @param htmlResourceVersion html resource version
     * @return message
     */
    ServiceResponse updateHtmlResourceVersion(HtmlResourceVersion htmlResourceVersion);

    /**
     * Service deletes html resource with all versions
     *
     * @param htmlResourceId ID of html resource
     * @return message
     */
    ServiceResponse deleteHtmlResource(Long htmlResourceId);

    /**
     * Service deletes html resource version
     *
     * @param htmlResourceId ID of html resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    ServiceResponse deleteHtmlResourceVersion(Long htmlResourceId, String culture, Integer version);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    IMAGE RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves image resource by ID for last version and american culture
     *
     * @param imageResourceId ID of image resource
     * @return found image resource
     */
    ServiceResponse<ImageResource> readImageResourceById(Long imageResourceId);

    /**
     * Service retrieves image resource by ID
     *
     * @param imageResourceId ID of image resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found image resource
     */
    ServiceResponse<ImageResource> readImageResourceByIdCultureAndVersion(Long imageResourceId, String culture, Integer version);

    ServiceResponse<AbstractResource> readResourcesByLookupList(ListLookup lookupList);

    /**
     * Service retrieves image resource by lookup
     *
     * @param lookup lookup of image resource
     * @return found image resource
     */
    ServiceResponse<ImageResource> readImageResourceByLookup(String lookup);

    InputStream readResourceImageStream(String lookup);

    /**
     * Service retrieves image resource file
     *
     * @param resourceId lookup of image resource
     * @param imageFilename temporary file name of uploaded image
     * @return found image resource
     */
    ServiceResponse<FileInfo> getImageResourceVersionFile(Long resourceId, String imageFilename);

    /**
     * Service allows to create a new image resource
     *
     * @param imageResource image resource for save
     * @return created image resource
     */
    ServiceResponse<ImageResource> createImageResource(ImageResource imageResource);

    /**
     * Service allows to create copy image resource versions from the last version but increase version number
     *
     * @param imageResourceId ID of image resource
     * @return image resource with new image resource version
     */
    ServiceResponse<ImageResource> createNewImageResourceVersions(Long imageResourceId);


    /**
     * Service allows to create copy image resource versions from defined version but increase version number
     *
     * @param imageResourceId ID of image resource
     * @param version version number from which need to create new ones
     * @return image resource with new image resource version
     */
    ServiceResponse<ImageResource> createNewImageResourceVersions(Long imageResourceId, Integer version);

    /**
     * Service updates image resource
     *
     * @param imageResource image resource which need to update
     * @return updated image resource
     */
    ServiceResponse<ImageResource> updateImageResource(ImageResource imageResource);

    /**
     * Service creates image resource version
     *
     * @param resourceLookup ID of image resource version
     * @param imageResourceVersion image resource version
     * @return message
     */
    ServiceResponse<ImageResourceVersion> createImageResourceVersionByLookup(String resourceLookup, ImageResourceVersion imageResourceVersion);

    /**
     * Service creates image resource version
     *
     * @param resourcePath ID of image resource version
     * @param imageResourceVersion image resource version
     * @return message
     */
    ServiceResponse<ImageResourceVersion> createImageResourceVersionByPath(String resourcePath, ImageResourceVersion imageResourceVersion);

    /**
     * Service updates image resource version
     *
     * @param imageResourceVersion image resource version
     * @return message
     */
    ServiceResponse<ImageResourceVersion> updateImageResourceVersion(ImageResourceVersion imageResourceVersion);

    /**
     * Service updates image resource version
     *
     * @param resourceLookup image resource lookup
     * @param imageResourceVersion image resource version
     * @return message
     */
    ServiceResponse<ImageResourceVersion> updateImageResourceVersionByLookup(String resourceLookup, ImageResourceVersion imageResourceVersion);

    /**
     * Service deletes image resource with all versions
     *
     * @param imageResourceId ID of image resource
     * @return message
     */
    ServiceResponse deleteImageResource(Long imageResourceId);

    /**
     * Service deletes image resource with all versions
     *
     * @param lookup ID of image resource
     * @return message
     */
    ServiceResponse deleteImageResourceByLookup(String lookup);

    /**
     * Service deletes image resource version
     *
     * @param imageResourceId ID of image resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    ServiceResponse deleteImageResourceVersion(Long imageResourceId, String culture, Integer version);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    AUDIO RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves audio resource by ID for last version and american culture
     *
     * @param audioResourceId ID of audio resource
     * @return found audio resource
     */
    ServiceResponse<AudioResource> readAudioResourceById(Long audioResourceId);

    /**
     * Service retrieves audio resource by ID
     *
     * @param audioResourceId ID of audio resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found audio resource
     */
    ServiceResponse<AudioResource> readAudioResourceByIdCultureAndVersion(Long audioResourceId, String culture, Integer version);

    /**
     * Service retrieves audio resource by lookup
     *
     * @param lookup lookup of audio resource
     * @return found audio resource
     */
    ServiceResponse<AudioResource> readAudioResourceByLookup(String lookup);

    /**
     * Service retrieves audio resource file
     *
     * @param resourceId lookup of audio resource
     * @param audioFilename temporary file name of uploaded audio
     * @return found audio resource
     */
    ServiceResponse<FileInfo> getAudioResourceVersionFile(Long resourceId, String audioFilename);

    /**
     * Service allows to create a new audio resource
     *
     * @param audioResource audio resource for save
     * @return created audio resource
     */
    ServiceResponse<AudioResource> createAudioResource(AudioResource audioResource);

    /**
     * Service allows to create copy audio resource versions from the last version but increase version number
     *
     * @param audioResourceId ID of audio resource
     * @return audio resource with new audio resource version
     */
    ServiceResponse<AudioResource> createNewAudioResourceVersions(Long audioResourceId);


    /**
     * Service allows to create copy audio resource versions from defined version but increase version number
     *
     * @param audioResourceId ID of audio resource
     * @param version version number from which need to create new ones
     * @return audio resource with new audio resource version
     */
    ServiceResponse<AudioResource> createNewAudioResourceVersions(Long audioResourceId, Integer version);

    /**
     * Service updates audio resource
     *
     * @param audioResource audio resource which need to update
     * @return updated audio resource
     */
    ServiceResponse<AudioResource> updateAudioResource(AudioResource audioResource);

    /**
     * Service updates audio resource version
     *
     * @param audioResourceVersion audio resource version
     * @return message
     */
    ServiceResponse updateAudioResourceVersion(AudioResourceVersion audioResourceVersion);

    /**
     * Service deletes audio resource with all versions
     *
     * @param audioResourceId ID of audio resource
     * @return message
     */
    ServiceResponse deleteAudioResource(Long audioResourceId);

    /**
     * Service deletes audio resource version
     *
     * @param audioResourceId ID of audio resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    ServiceResponse deleteAudioResourceVersion(Long audioResourceId, String culture, Integer version);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    VIDEO RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves video resource by ID for last version and american culture
     *
     * @param videoResourceId ID of video resource
     * @return found video resource
     */
    ServiceResponse<VideoResource> readVideoResourceById(Long videoResourceId);

    /**
     * Service retrieves video resource by ID
     *
     * @param videoResourceId ID of video resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found video resource
     */
    ServiceResponse<VideoResource> readVideoResourceByIdCultureAndVersion(Long videoResourceId, String culture, Integer version);

    /**
     * Service retrieves video resource by lookup
     *
     * @param lookup lookup of video resource
     * @return found video resource
     */
    ServiceResponse<VideoResource> readVideoResourceByLookup(String lookup);

    /**
     * Service retrieves video resource file
     *
     * @param resourceId lookup of video resource
     * @param videoFilename temporary file name of uploaded video
     * @return found video resource
     */
    ServiceResponse<FileInfo> getVideoResourceVersionFile(Long resourceId, String videoFilename);

    /**
     * Service allows to create a new video resource
     *
     * @param videoResource video resource for save
     * @return created video resource
     */
    ServiceResponse<VideoResource> createVideoResource(VideoResource videoResource);

    /**
     * Service allows to create copy video resource versions from the last version but increase version number
     *
     * @param videoResourceId ID of video resource
     * @return video resource with new video resource version
     */
    ServiceResponse<VideoResource> createNewVideoResourceVersions(Long videoResourceId);


    /**
     * Service allows to create copy video resource versions from defined version but increase version number
     *
     * @param videoResourceId ID of video resource
     * @param version version number from which need to create new ones
     * @return video resource with new video resource version
     */
    ServiceResponse<VideoResource> createNewVideoResourceVersions(Long videoResourceId, Integer version);

    /**
     * Service updates video resource
     *
     * @param videoResource video resource which need to update
     * @return updated video resource
     */
    ServiceResponse<VideoResource> updateVideoResource(VideoResource videoResource);

    /**
     * Service updates video resource version
     *
     * @param videoResourceVersion video resource version
     * @return message
     */
    ServiceResponse updateVideoResourceVersion(VideoResourceVersion videoResourceVersion);

    /**
     * Service deletes video resource with all versions
     *
     * @param videoResourceId ID of video resource
     * @return message
     */
    ServiceResponse deleteVideoResource(Long videoResourceId);

    /**
     * Service deletes video resource version
     *
     * @param videoResourceId ID of video resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    ServiceResponse deleteVideoResourceVersion(Long videoResourceId, String culture, Integer version);

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    DOCUMENT RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves document resource by ID for last version and american culture
     *
     * @param documentResourceId ID of document resource
     * @return found document resource
     */
    ServiceResponse<DocumentResource> readDocumentResourceById(Long documentResourceId);

    /**
     * Service retrieves document resource by ID
     *
     * @param documentResourceId ID of document resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found document resource
     */
    ServiceResponse<DocumentResource> readDocumentResourceByIdCultureAndVersion(Long documentResourceId, String culture, Integer version);

    /**
     * Service retrieves document resource by lookup
     *
     * @param lookup lookup of document resource
     * @return found document resource
     */
    ServiceResponse<DocumentResource> readDocumentResourceByLookup(String lookup);

    /**
     * Service retrieves document resource file
     *
     * @param resourceId lookup of document resource
     * @param documentFilename temporary file name of uploaded document
     * @return found document resource
     */
    ServiceResponse<FileInfo> getDocumentResourceVersionFile(Long resourceId, String documentFilename);

    /**
     * Service allows to create a new document resource
     *
     * @param documentResource document resource for save
     * @return created document resource
     */
    ServiceResponse<DocumentResource> createDocumentResource(DocumentResource documentResource);

    /**
     * Service allows to create copy document resource versions from the last version but increase version number
     *
     * @param documentResourceId ID of document resource
     * @return document resource with new document resource version
     */
    ServiceResponse<DocumentResource> createNewDocumentResourceVersions(Long documentResourceId);


    /**
     * Service allows to create copy document resource versions from defined version but increase version number
     *
     * @param documentResourceId ID of document resource
     * @param version version number from which need to create new ones
     * @return document resource with new document resource version
     */
    ServiceResponse<DocumentResource> createNewDocumentResourceVersions(Long documentResourceId, Integer version);

    /**
     * Service updates document resource
     *
     * @param documentResource document resource which need to update
     * @return updated document resource
     */
    ServiceResponse<DocumentResource> updateDocumentResource(DocumentResource documentResource);

    /**
     * Service updates document resource version
     *
     * @param documentResourceVersion document resource version
     * @return message
     */
    ServiceResponse updateDocumentResourceVersion(DocumentResourceVersion documentResourceVersion);

    /**
     * Service deletes document resource with all versions
     *
     * @param documentResourceId ID of document resource
     * @return message
     */
    ServiceResponse deleteDocumentResource(Long documentResourceId);

    /**
     * Service deletes document resource version
     *
     * @param documentResourceId ID of document resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    ServiceResponse deleteDocumentResourceVersion(Long documentResourceId, String culture, Integer version);

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    DOCUMENT RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Service retrieves file resource by ID for last version and american culture
     *
     * @param fileResourceId ID of file resource
     * @return found file resource
     */
    ServiceResponse<FileResource> readFileResourceById(Long fileResourceId);

    /**
     * Service retrieves file resource by ID
     *
     * @param fileResourceId ID of file resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return found file resource
     */
    ServiceResponse<FileResource> readFileResourceByIdCultureAndVersion(Long fileResourceId, String culture, Integer version);

    /**
     * Service retrieves file resource by lookup
     *
     * @param lookup lookup of file resource
     * @return found file resource
     */
    ServiceResponse<FileResource> readFileResourceByLookup(String lookup);

    /**
     * Service retrieves file resource file
     *
     * @param resourceId lookup of file resource
     * @param fileFilename temporary file name of uploaded file
     * @return found file resource
     */
    ServiceResponse<FileInfo> getFileResourceVersionFile(Long resourceId, String fileFilename);

    /**
     * Service allows to create a new file resource
     *
     * @param fileResource file resource for save
     * @return created file resource
     */
    ServiceResponse<FileResource> createFileResource(FileResource fileResource);

    /**
     * Service allows to create copy file resource versions from the last version but increase version number
     *
     * @param fileResourceId ID of file resource
     * @return file resource with new file resource version
     */
    ServiceResponse<FileResource> createNewFileResourceVersions(Long fileResourceId);


    /**
     * Service allows to create copy file resource versions from defined version but increase version number
     *
     * @param fileResourceId ID of file resource
     * @param version version number from which need to create new ones
     * @return file resource with new file resource version
     */
    ServiceResponse<FileResource> createNewFileResourceVersions(Long fileResourceId, Integer version);

    /**
     * Service updates file resource
     *
     * @param fileResource file resource which need to update
     * @return updated file resource
     */
    ServiceResponse<FileResource> updateFileResource(FileResource fileResource);

    /**
     * Service updates file resource version
     *
     * @param fileResourceVersion file resource version
     * @return message
     */
    ServiceResponse updateFileResourceVersion(FileResourceVersion fileResourceVersion);

    /**
     * Service deletes file resource with all versions
     *
     * @param fileResourceId ID of file resource
     * @return message
     */
    ServiceResponse deleteFileResource(Long fileResourceId);

    /**
     * Service deletes file resource version
     *
     * @param fileResourceId ID of file resource
     * @param culture is one of value from Cultures enum
     * @param version version of resources
     * @return message
     */
    ServiceResponse deleteFileResourceVersion(Long fileResourceId, String culture, Integer version);

	/**
	 * Create new documentation
	 *
	 * @param data documentation data
	 *
	 * @return created document
	 */
	ServiceResponse<ResourceContent> createDocumentation(ResourceContent data);

		/**
	 * Update documentation by id
	 *
	 * @param resourceVersionId documentation id
	 * @param data documentation data
	 *
	 * @return updated documentation
	 */
	ServiceResponse<ResourceContent> updateDocumentation(Long resourceVersionId, ResourceContent data);

	/**
	 * Updated documentation image by id
	 * @param resourceVersionId documentation id
	 * @param data image resource data
	 *
	 * @return updated documentation image
	 */
	ServiceResponse updateDocumentationImage(Long resourceVersionId, ImageResourceVersion data);

		/**
	 * Delete documentation by id
	 *
	 * @param resourceId documentation id
	 *
	 * @return deleted documentation
	 */
	ServiceResponse deleteDocumentation(Long resourceId);

	/**
	 * Read entity service links
	 *
	 * @param registryNodeId node id
	 *
	 * @return founded entity links
	 */
	ServiceResponse<ActionServiceLink> readEntityServiceLinks(Long registryNodeId);

	/**
	 * Read examples by parent id
	 *
	 * @param registryNodeId parent id	 *
	 * @param country country
	 *
	 * @return founded examples
	 */
	ServiceResponse<ActionServiceLink> readExamplesByParentId(Long registryNodeId, String country);

	/**
	 * Read descriptions by parent id
	 *
	 * @param nodeId parent id
	 * @param country country
	 *
	 * @return founded descriptions
	 */
	ServiceResponse<AbstractResource> readDescriptionsByParentId(Long nodeId, String country);

	/**
	 * Read fields  by parent id
	 * @param nodeId parent id
	 * @param country country
	 *
	 * @return founded fields
	 */
	ServiceResponse<FieldResource> readFields(Long nodeId, String country);

	/**
	 * Read Related Entities by parent id
	 *
	 * @param nodeId parent id
	 * @param country country
	 *
	 * @return founded Related Entities
	 */
	ServiceResponse<EntityResourceRelationship> readRelatedEntities(Long nodeId, String country);

	/**
	 * Read actions by parent id
	 *
	 * @param nodeId parent id
	 * @param country country
	 *
	 * @return founded actions
	 */
	ServiceResponse<ActionResource> readActions(Long nodeId, String country);

	/**
	 * Read children by parent id
	 *
	 *
	 * @param nodeId parent id
	 *
	 * @return founded children
	 */
	ServiceResponse<Lookup> readChildren(Long nodeId);

	/**
	 * Read parents by children id
	 *
	 *
	 * @param nodeId children id
	 *
	 * @return founded parent
	 */
	ServiceResponse<Lookup> readParents(Long nodeId);

	/**
	 * Read documentation page
	 *
	 * @param country country
	 * @param lookup lookup
	 * @param maxImageWidth max image width
	 * @param token token
	 *
	 * @return founded documentation
	 */
	ServiceResponse getDocumentationHtmlSource(String lookup, String country, Integer maxImageWidth, String token);

	/**
	 * Generate  documentation example
	 * @return Generate status
	 */
	ServiceResponse generateDocumentationExample(String lookup);
}
