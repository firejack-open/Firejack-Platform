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

package net.firejack.platform.service.content;

import net.firejack.platform.api.APIConstants;
import net.firejack.platform.api.content.IContentService;
import net.firejack.platform.api.content.domain.*;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.ListLookup;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.domain.NamedValues;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.service.content.broker.collection.*;
import net.firejack.platform.service.content.broker.documentation.*;
import net.firejack.platform.service.content.broker.folder.*;
import net.firejack.platform.service.content.broker.resource.*;
import net.firejack.platform.service.content.broker.resource.audio.*;
import net.firejack.platform.service.content.broker.resource.document.*;
import net.firejack.platform.service.content.broker.resource.file.*;
import net.firejack.platform.service.content.broker.resource.html.*;
import net.firejack.platform.service.content.broker.resource.image.*;
import net.firejack.platform.service.content.broker.resource.text.*;
import net.firejack.platform.service.content.broker.resource.video.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;


@SuppressWarnings("unused")
@Component(APIConstants.BEAN_NAME_CONTENT_SERVICE)
public class ContentServiceLocal implements IContentService {

    @Autowired
    @Qualifier("readFolderBroker")
    private ReadFolderBroker readFolderBroker;

    @Autowired
    @Qualifier("readChildFoldersByParentIdBroker")
    private ReadFoldersByParentIdBroker readChildFoldersByParentIdBroker;

    @Autowired
    @Qualifier("readFoldersByLikeLookupBroker")
    private ReadFoldersByLikeLookupBroker readFoldersByLikeLookupBroker;

    @Autowired
    @Qualifier("createFolderBroker")
    private CreateFolderBroker createFolderBroker;

    @Autowired
    @Qualifier("updateFolderBroker")
    private UpdateFolderBroker updateFolderBroker;

    @Autowired
    @Qualifier("deleteFolderBroker")
    private DeleteFolderBroker deleteFolderBroker;

    @Autowired
    @Qualifier("readCollectionBroker")
    private ReadCollectionBroker readCollectionBroker;

    @Autowired
    @Qualifier("readCollectionsByParentIdBroker")
    private ReadCollectionsByParentIdBroker readCollectionsByParentIdBroker;

    @Autowired
    @Qualifier("readCollectionsByLikeLookupBroker")
    private ReadCollectionsByLikeLookupBroker readCollectionsByLikeLookupBroker;

    @Autowired
    @Qualifier("createCollectionBroker")
    private CreateCollectionBroker createCollectionBroker;

    @Autowired
    @Qualifier("updateCollectionBroker")
    private UpdateCollectionBroker updateCollectionBroker;

    @Autowired
    @Qualifier("deleteCollectionBroker")
    private DeleteCollectionBroker deleteCollectionBroker;

    @Autowired
    @Qualifier("exportCollectionArchiveFileBroker")
    private ExportCollectionArchiveFileBroker exportCollectionArchiveFileBroker;

    @Autowired
    @Qualifier("importCollectionArchiveFileBroker")
    private ImportCollectionArchiveFileBroker importCollectionArchiveFileBroker;

    @Autowired
    @Qualifier("swapCollectionBroker")
    private SwapCollectionBroker swapCollectionBroker;

    @Autowired
    @Qualifier("readAllCulturesBroker")
    private ReadAllCulturesBroker readAllCulturesBroker;

    @Autowired
    @Qualifier("readAvailableCulturesBroker")
    private ReadAvailableCulturesBroker readAvailableCulturesBroker;

    @Autowired
    @Qualifier("readResourcesByParentIdBroker")
    private ReadResourcesByParentIdBroker readResourcesByParentIdBroker;

    @Autowired
    @Qualifier("searchResourcesByTermBroker")
    private SearchResourcesByTermBroker searchResourcesByTermBroker;

    @Autowired
    @Qualifier("uploadResourceFileBroker")
    private UploadResourceFileBroker uploadResourceFileBroker;

    @Autowired
    @Qualifier("getTemporaryUploadedFileBroker")
    private GetTemporaryUploadedFileBroker getTemporaryUploadedFileBroker;

    @Autowired
    @Qualifier("readTextResourceByIdBroker")
    private ReadTextResourceByIdBroker readTextResourceByIdBroker;

    @Autowired
    @Qualifier("readTextResourceByLookupBroker")
    private ReadTextResourceByLookupBroker readTextResourceByLookupBroker;

    @Autowired
    @Qualifier("saveTextResourceBroker")
    private SaveTextResourceBroker saveTextResourceBroker;

    @Autowired
    @Qualifier("createTextResourceVersionBroker")
    private CreateTextResourceVersionBroker createTextResourceVersionBroker;

    @Autowired
    @Qualifier("updateTextResourceVersionBroker")
    private UpdateTextResourceVersionBroker updateTextResourceVersionBroker;

    @Autowired
    @Qualifier("deleteTextResourceBroker")
    private DeleteTextResourceBroker deleteTextResourceBroker;

    @Autowired
    @Qualifier("deleteTextResourceVersionBroker")
    private DeleteTextResourceVersionBroker deleteTextResourceVersionBroker;

    @Autowired
    @Qualifier("createNewTextResourceVersionsBroker")
    private CreateNewTextResourceVersionsBroker createNewTextResourceVersionsBroker;

    @Autowired
    @Qualifier("readHtmlResourceByIdBroker")
    private ReadHtmlResourceByIdBroker readHtmlResourceByIdBroker;

    @Autowired
    @Qualifier("readHtmlResourceByLookupBroker")
    private ReadHtmlResourceByLookupBroker readHtmlResourceByLookupBroker;

    @Autowired
    @Qualifier("saveHtmlResourceBroker")
    private SaveHtmlResourceBroker saveHtmlResourceBroker;

    @Autowired
    @Qualifier("updateHtmlResourceVersionBroker")
    private UpdateHtmlResourceVersionBroker updateHtmlResourceVersionBroker;

    @Autowired
    @Qualifier("deleteHtmlResourceBroker")
    private DeleteHtmlResourceBroker deleteHtmlResourceBroker;

    @Autowired
    @Qualifier("deleteHtmlResourceVersionBroker")
    private DeleteHtmlResourceVersionBroker deleteHtmlResourceVersionBroker;

    @Autowired
    @Qualifier("createNewHtmlResourceVersionsBroker")
    private CreateNewHtmlResourceVersionsBroker createNewHtmlResourceVersionsBroker;

    @Autowired
    @Qualifier("readImageResourceByIdBroker")
    private ReadImageResourceByIdBroker readImageResourceByIdBroker;

	@Autowired
	private ReadResourcesByLookupList readResourcesByLookupList;

    @Autowired
    @Qualifier("readImageResourceByLookupBroker")
    private ReadImageResourceByLookupBroker readImageResourceByLookupBroker;
	@Autowired
	private ReadResourceImageStreamBroker readResourceImageStreamBroker;

    @Autowired
    @Qualifier("getImageResourceVersionFileBroker")
    private GetImageResourceVersionFileBroker getImageResourceVersionFileBroker;

    @Autowired
    @Qualifier("saveImageResourceBroker")
    private SaveImageResourceBroker saveImageResourceBroker;

    @Autowired
    @Qualifier("createImageResourceVersionByLookupBroker")
    private CreateImageResourceVersionByLookupBroker createImageResourceVersionByLookupBroker;

    @Autowired
    @Qualifier("createImageResourceVersionByPathBroker")
    private CreateImageResourceVersionByLookupBroker createImageResourceVersionByPathBroker;

    @Autowired
    @Qualifier("updateImageResourceVersionBroker")
    private UpdateImageResourceVersionBroker updateImageResourceVersionBroker;

    @Autowired
    @Qualifier("deleteImageResourceBroker")
    private DeleteImageResourceBroker deleteImageResourceBroker;

    @Autowired
    @Qualifier("deleteImageResourceByLookupBroker")
    private DeleteImageResourceByLookupBroker deleteImageResourceByLookupBroker;

    @Autowired
    @Qualifier("deleteImageResourceVersionBroker")
    private DeleteImageResourceVersionBroker deleteImageResourceVersionBroker;

    @Autowired
    @Qualifier("createNewImageResourceVersionsBroker")
    private CreateNewImageResourceVersionsBroker createNewImageResourceVersionsBroker;

    @Autowired
    @Qualifier("readAudioResourceByIdBroker")
    private ReadAudioResourceByIdBroker readAudioResourceByIdBroker;

    @Autowired
    @Qualifier("readAudioResourceByLookupBroker")
    private ReadAudioResourceByLookupBroker readAudioResourceByLookupBroker;

    @Autowired
    @Qualifier("getAudioResourceVersionFileBroker")
    private GetAudioResourceVersionFileBroker getAudioResourceVersionFileBroker;

    @Autowired
    @Qualifier("saveAudioResourceBroker")
    private SaveAudioResourceBroker saveAudioResourceBroker;

    @Autowired
    @Qualifier("updateAudioResourceVersionBroker")
    private UpdateAudioResourceVersionBroker updateAudioResourceVersionBroker;

    @Autowired
    @Qualifier("deleteAudioResourceBroker")
    private DeleteAudioResourceBroker deleteAudioResourceBroker;

    @Autowired
    @Qualifier("deleteAudioResourceVersionBroker")
    private DeleteAudioResourceVersionBroker deleteAudioResourceVersionBroker;

    @Autowired
    @Qualifier("createNewAudioResourceVersionsBroker")
    private CreateNewAudioResourceVersionsBroker createNewAudioResourceVersionsBroker;

    @Autowired
    @Qualifier("readVideoResourceByIdBroker")
    private ReadVideoResourceByIdBroker readVideoResourceByIdBroker;
    @Autowired
    @Qualifier("readVideoResourceByLookupBroker")
    private ReadVideoResourceByLookupBroker readVideoResourceByLookupBroker;
    @Autowired
    @Qualifier("getVideoResourceVersionFileBroker")
    private GetVideoResourceVersionFileBroker getVideoResourceVersionFileBroker;
    @Autowired
    @Qualifier("saveVideoResourceBroker")
    private SaveVideoResourceBroker saveVideoResourceBroker;
    @Autowired
    @Qualifier("updateVideoResourceVersionBroker")
    private UpdateVideoResourceVersionBroker updateVideoResourceVersionBroker;
    @Autowired
    @Qualifier("deleteVideoResourceBroker")
    private DeleteVideoResourceBroker deleteVideoResourceBroker;
    @Autowired
    @Qualifier("deleteVideoResourceVersionBroker")
    private DeleteVideoResourceVersionBroker deleteVideoResourceVersionBroker;
    @Autowired
    @Qualifier("createNewVideoResourceVersionsBroker")
    private CreateNewVideoResourceVersionsBroker createNewVideoResourceVersionsBroker;

    @Autowired
    @Qualifier("readDocumentResourceByIdBroker")
    private ReadDocumentResourceByIdBroker readDocumentResourceByIdBroker;
    @Autowired
    @Qualifier("readDocumentResourceByLookupBroker")
    private ReadDocumentResourceByLookupBroker readDocumentResourceByLookupBroker;
    @Autowired
    @Qualifier("getDocumentResourceVersionFileBroker")
    private GetDocumentResourceVersionFileBroker getDocumentResourceVersionFileBroker;
    @Autowired
    @Qualifier("saveDocumentResourceBroker")
    private SaveDocumentResourceBroker saveDocumentResourceBroker;
    @Autowired
    @Qualifier("updateDocumentResourceVersionBroker")
    private UpdateDocumentResourceVersionBroker updateDocumentResourceVersionBroker;
    @Autowired
    @Qualifier("deleteDocumentResourceBroker")
    private DeleteDocumentResourceBroker deleteDocumentResourceBroker;
    @Autowired
    @Qualifier("deleteDocumentResourceVersionBroker")
    private DeleteDocumentResourceVersionBroker deleteDocumentResourceVersionBroker;
    @Autowired
    @Qualifier("createNewDocumentResourceVersionsBroker")
    private CreateNewDocumentResourceVersionsBroker createNewDocumentResourceVersionsBroker;

    @Autowired
    @Qualifier("readFileResourceByIdBroker")
    private ReadFileResourceByIdBroker readFileResourceByIdBroker;
    @Autowired
    @Qualifier("readFileResourceByLookupBroker")
    private ReadFileResourceByLookupBroker readFileResourceByLookupBroker;
    @Autowired
    @Qualifier("getFileResourceVersionFileBroker")
    private GetFileResourceVersionFileBroker getFileResourceVersionFileBroker;
    @Autowired
    @Qualifier("saveFileResourceBroker")
    private SaveFileResourceBroker saveFileResourceBroker;
    @Autowired
    @Qualifier("updateFileResourceVersionBroker")
    private UpdateFileResourceVersionBroker updateFileResourceVersionBroker;
    @Autowired
    @Qualifier("deleteFileResourceBroker")
    private DeleteFileResourceBroker deleteFileResourceBroker;
    @Autowired
    @Qualifier("deleteFileResourceVersionBroker")
    private DeleteFileResourceVersionBroker deleteFileResourceVersionBroker;
    @Autowired
    @Qualifier("createNewFileResourceVersionsBroker")
    private CreateNewFileResourceVersionsBroker createNewFileResourceVersionsBroker;

	@Autowired
	private CreateResourceDescriptionBroker createResourceDescriptionBroker;
	@Autowired
	private UpdateResourceDescriptionBroker updateResourceDescriptionBroker;
	@Autowired
	private UpdateDocumentationImageBroker updateDocumentationImageBroker;
	@Autowired
	private DeleteResourceDescriptionBroker deleteResourceDescriptionBroker;
	@Autowired
	private ReadEntityServiceLinksBroker readEntityServiceLinksBroker;
	@Autowired
	private ReadDescriptionListByParentIdBroker readDescriptionListByParentIdBroker;
	@Autowired
	private ReadFieldWithResourcesListBroker readFieldWithResourcesListBroker;
	@Autowired
	private ReadRelatedEntityWithResourcesListBroker readRelatedEntityWithResourcesListBroker;
	@Autowired
	private ReadActionWithResourcesListBroker readActionWithResourcesListBroker;
	@Autowired
	private ReadRegistryNodeDocumentationChildrenListBroker readRegistryNodeDocumentationChildrenListBroker;
	@Autowired
	private ReadRegistryNodeParentListBroker readRegistryNodeParentListBroker;
	@Autowired
	private GetDocumentationPageBroker getDocumentationPageBroker;
	@Autowired
	private GenerateDocumentationExampleBroker generateDocumentationExampleBroker;
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//   FOLDER SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<Folder> readFolder(Long folderId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(folderId);
        return readFolderBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Folder> readFoldersByParentId(Long parentId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(parentId);
        return readChildFoldersByParentIdBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Folder> readFoldersByLikeLookup(String lookup) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(lookup);
        return readFoldersByLikeLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<RegistryNodeTree> createFolder(Folder folder) {
        return createFolderBroker.execute(new ServiceRequest<Folder>(folder));
    }

    @Override
    public ServiceResponse<RegistryNodeTree> updateFolder(Folder folder) {
        return updateFolderBroker.execute(new ServiceRequest<Folder>(folder));
    }

    @Override
    public ServiceResponse deleteFolder(Long folderId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(folderId);
        return deleteFolderBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Collection> readCollection(Long collectionId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(collectionId);
        return readCollectionBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Collection> readCollectionsByParentId(Long parentId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(parentId);
        return readCollectionsByParentIdBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Collection> readCollectionsByLikeLookup(String lookup) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(lookup);
        return readCollectionsByLikeLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<RegistryNodeTree> createCollection(Collection collection) {
        return createCollectionBroker.execute(new ServiceRequest<Collection>(collection));
    }

    @Override
    public ServiceResponse<RegistryNodeTree> updateCollection(Collection collection) {
        return updateCollectionBroker.execute(new ServiceRequest<Collection>(collection));
    }

    @Override
    public ServiceResponse deleteCollection(Long collectionId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(collectionId);
        return deleteCollectionBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<FileInfo> exportCollectionArchiveFile(Long collectionId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(collectionId);
        return exportCollectionArchiveFileBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse importCollectionArchiveFile(InputStream inputStream) {
        NamedValues<InputStream> namedValues = new NamedValues<InputStream>();
        namedValues.put("inputStream", inputStream);
        return importCollectionArchiveFileBroker.execute(new ServiceRequest<NamedValues<InputStream>>(namedValues));
    }

    @Override
    public ServiceResponse swapCollectionMemberships(Long collectionId, Long oneRefId, Long twoRefId) {
        NamedValues<Long> namedValues = new NamedValues<Long>();
		namedValues.put("collectionId", collectionId);
		namedValues.put("oneRefId", oneRefId);
		namedValues.put("twoRefId", twoRefId);
        return swapCollectionBroker.execute(new ServiceRequest<NamedValues<Long>>(namedValues));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<Culture> readAllCultures() {
        return readAllCulturesBroker.execute(new ServiceRequest());
    }

    @Override
    public ServiceResponse<Culture> readAvailableCultures(Long entityId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(entityId);
        return readAvailableCulturesBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<Resource> readResourcesByParentId(Long registryNodeId, List<Long> exceptIds, Paging paging) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("registryNodeId", registryNodeId);
		namedValues.put("exceptIds", exceptIds);
		namedValues.put("paging", paging);
        return readResourcesByParentIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<Resource> searchResourcesByParentIdAndTerm(Long registryNodeId, String term) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("registryNodeId", registryNodeId);
		namedValues.put("term", term);
        return searchResourcesByTermBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<FileInfo> uploadResourceFile(String resourceType, String customTTL, InputStream inputStream, String originalFilename) {
        NamedValues<Object> namedValues = new NamedValues<Object>();
		namedValues.put(UploadResourceFileBroker.PARAM_RESOURCE_TYPE, resourceType);
        namedValues.put(UploadResourceFileBroker.PARAM_CUSTOM_TTL, customTTL);
        namedValues.put(UploadResourceFileBroker.PARAM_INPUT_STREAM, inputStream);
        namedValues.put(UploadResourceFileBroker.PARAM_FILENAME, originalFilename);
        return uploadResourceFileBroker.execute(new ServiceRequest<NamedValues<Object>>(namedValues));
    }

    @Override
    public ServiceResponse<FileInfo> getTemporaryUploadedFile(String temporaryFilename) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(temporaryFilename);
        return getTemporaryUploadedFileBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    TEXT RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<TextResource> readTextResourceById(Long textResourceId) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", textResourceId);
        return readTextResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<TextResource> readTextResourceByIdCultureAndVersion(Long textResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", textResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return readTextResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<TextResource> readTextResourceByLookup(String lookup) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(lookup);
        return readTextResourceByLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<TextResource> createTextResource(TextResource textResource) {
        return saveTextResourceBroker.execute(new ServiceRequest<TextResource>(textResource));
    }

    @Override
    public ServiceResponse<TextResource> createNewTextResourceVersions(Long textResourceId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", textResourceId);
        return createNewTextResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<TextResource> createNewTextResourceVersions(Long textResourceId, Integer version) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", textResourceId);
        namedValues.put("version", version);
        return createNewTextResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<TextResource> updateTextResource(TextResource textResource) {
        return saveTextResourceBroker.execute(new ServiceRequest<TextResource>(textResource));
    }

    @Override
    public ServiceResponse<TextResourceVersion> createTextResourceVersion(String resourceLookup, TextResourceVersion textResourceVersion) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceLookup", resourceLookup);
		namedValues.put("textResourceVersion", textResourceVersion);
        return createTextResourceVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<TextResourceVersion> updateTextResourceVersion(TextResourceVersion textResourceVersion) {
        ServiceRequest<TextResourceVersion> request = new ServiceRequest<TextResourceVersion>(textResourceVersion);
        return updateTextResourceVersionBroker.execute(request);
    }

    @Override
    public ServiceResponse deleteTextResource(Long textResourceId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(textResourceId);
        return deleteTextResourceBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse deleteTextResourceVersion(Long textResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", textResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return deleteTextResourceVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    HTML RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<HtmlResource> readHtmlResourceById(Long htmlResourceId) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", htmlResourceId);
        return readHtmlResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<HtmlResource> readHtmlResourceByIdCultureAndVersion(Long htmlResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", htmlResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return readHtmlResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<HtmlResource> readHtmlResourceByLookup(String lookup) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(lookup);
        return readHtmlResourceByLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<HtmlResource> createHtmlResource(HtmlResource htmlResource) {
        return saveHtmlResourceBroker.execute(new ServiceRequest<HtmlResource>(htmlResource));
    }

    @Override
    public ServiceResponse<HtmlResource> createNewHtmlResourceVersions(Long htmlResourceId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", htmlResourceId);
        return createNewHtmlResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<HtmlResource> createNewHtmlResourceVersions(Long htmlResourceId, Integer version) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", htmlResourceId);
        namedValues.put("version", version);
        return createNewHtmlResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<HtmlResource> updateHtmlResource(HtmlResource htmlResource) {
        return saveHtmlResourceBroker.execute(new ServiceRequest<HtmlResource>(htmlResource));
    }

    @Override
    public ServiceResponse updateHtmlResourceVersion(HtmlResourceVersion htmlResourceVersion) {
        ServiceRequest<HtmlResourceVersion> request = new ServiceRequest<HtmlResourceVersion>(htmlResourceVersion);
        return updateHtmlResourceVersionBroker.execute(request);
    }

    @Override
    public ServiceResponse deleteHtmlResource(Long htmlResourceId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(htmlResourceId);
        return deleteHtmlResourceBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse deleteHtmlResourceVersion(Long htmlResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", htmlResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return deleteHtmlResourceVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    IMAGE RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<ImageResource> readImageResourceById(Long imageResourceId) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", imageResourceId);
        return readImageResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<ImageResource> readImageResourceByIdCultureAndVersion(Long imageResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", imageResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return readImageResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<AbstractResource> readResourcesByLookupList(ListLookup lookupList) {
        return readResourcesByLookupList.execute(new ServiceRequest<ListLookup>(lookupList));
    }

    @Override
    public ServiceResponse<ImageResource> readImageResourceByLookup(String lookup) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(lookup);
        return readImageResourceByLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

	@Override
	public InputStream readResourceImageStream(String lookup) {
		ServiceResponse<SimpleIdentifier<InputStream>> response = readResourceImageStreamBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(lookup)));
		SimpleIdentifier<InputStream> item = response.getItem();
		return item != null ? item.getIdentifier() : null;
	}

	@Override
    public ServiceResponse<FileInfo> getImageResourceVersionFile(Long imageResourceId, String imageFilename) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", imageResourceId);
		namedValues.put("imageFilename", imageFilename);
        return getImageResourceVersionFileBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<ImageResource> createImageResource(ImageResource imageResource) {
        return saveImageResourceBroker.execute(new ServiceRequest<ImageResource>(imageResource));
    }

    @Override
    public ServiceResponse<ImageResource> createNewImageResourceVersions(Long imageResourceId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", imageResourceId);
        return createNewImageResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<ImageResource> createNewImageResourceVersions(Long imageResourceId, Integer version) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", imageResourceId);
        namedValues.put("version", version);
        return createNewImageResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<ImageResource> updateImageResource(ImageResource imageResource) {
        return saveImageResourceBroker.execute(new ServiceRequest<ImageResource>(imageResource));
    }

    @Override
    public ServiceResponse<ImageResourceVersion> createImageResourceVersionByLookup(String resourceLookup, ImageResourceVersion imageResourceVersion) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceLookup", resourceLookup);
		namedValues.put("imageResourceVersion", imageResourceVersion);
        return createImageResourceVersionByLookupBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<ImageResourceVersion> createImageResourceVersionByPath(String resourcePath, ImageResourceVersion imageResourceVersion) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourcePath", resourcePath);
		namedValues.put("imageResourceVersion", imageResourceVersion);
        return createImageResourceVersionByPathBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<ImageResourceVersion> updateImageResourceVersion(ImageResourceVersion imageResourceVersion) {
        ServiceRequest<ImageResourceVersion> request = new ServiceRequest<ImageResourceVersion>(imageResourceVersion);
        return updateImageResourceVersionBroker.execute(request);
    }

    @Override
    public ServiceResponse<ImageResourceVersion> updateImageResourceVersionByLookup(String resourceLookup, ImageResourceVersion imageResourceVersion) {
        imageResourceVersion.setResourceLookup(resourceLookup);
        ServiceRequest<ImageResourceVersion> request = new ServiceRequest<ImageResourceVersion>(imageResourceVersion);
        return updateImageResourceVersionBroker.execute(request);
    }

    @Override
    public ServiceResponse deleteImageResource(Long imageResourceId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(imageResourceId);
        return deleteImageResourceBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse deleteImageResourceByLookup(String lookup) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(lookup);
        return deleteImageResourceByLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse deleteImageResourceVersion(Long imageResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", imageResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return deleteImageResourceVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    AUDIO RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<AudioResource> readAudioResourceById(Long audioResourceId) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", audioResourceId);
        return readAudioResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<AudioResource> readAudioResourceByIdCultureAndVersion(Long audioResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", audioResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return readAudioResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<AudioResource> readAudioResourceByLookup(String lookup) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(lookup);
        return readAudioResourceByLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<FileInfo> getAudioResourceVersionFile(Long audioResourceId, String audioFilename) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", audioResourceId);
		namedValues.put("audioFilename", audioFilename);
        return getAudioResourceVersionFileBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<AudioResource> createAudioResource(AudioResource audioResource) {
        return saveAudioResourceBroker.execute(new ServiceRequest<AudioResource>(audioResource));
    }

    @Override
    public ServiceResponse<AudioResource> createNewAudioResourceVersions(Long audioResourceId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", audioResourceId);
        return createNewAudioResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<AudioResource> createNewAudioResourceVersions(Long audioResourceId, Integer version) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", audioResourceId);
        namedValues.put("version", version);
        return createNewAudioResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<AudioResource> updateAudioResource(AudioResource audioResource) {
        return saveAudioResourceBroker.execute(new ServiceRequest<AudioResource>(audioResource));
    }

    @Override
    public ServiceResponse updateAudioResourceVersion(AudioResourceVersion audioResourceVersion) {
        ServiceRequest<AudioResourceVersion> request = new ServiceRequest<AudioResourceVersion>(audioResourceVersion);
        return updateAudioResourceVersionBroker.execute(request);
    }

    @Override
    public ServiceResponse deleteAudioResource(Long audioResourceId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(audioResourceId);
        return deleteAudioResourceBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse deleteAudioResourceVersion(Long audioResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", audioResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return deleteAudioResourceVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    VIDEO RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<VideoResource> readVideoResourceById(Long videoResourceId) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", videoResourceId);
        return readVideoResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<VideoResource> readVideoResourceByIdCultureAndVersion(Long videoResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", videoResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return readVideoResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<VideoResource> readVideoResourceByLookup(String lookup) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(lookup);
        return readVideoResourceByLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<FileInfo> getVideoResourceVersionFile(Long videoResourceId, String videoFilename) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", videoResourceId);
		namedValues.put("videoFilename", videoFilename);
        return getVideoResourceVersionFileBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<VideoResource> createVideoResource(VideoResource videoResource) {
        return saveVideoResourceBroker.execute(new ServiceRequest<VideoResource>(videoResource));
    }

    @Override
    public ServiceResponse<VideoResource> createNewVideoResourceVersions(Long videoResourceId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", videoResourceId);
        return createNewVideoResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<VideoResource> createNewVideoResourceVersions(Long videoResourceId, Integer version) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", videoResourceId);
        namedValues.put("version", version);
        return createNewVideoResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<VideoResource> updateVideoResource(VideoResource videoResource) {
        return saveVideoResourceBroker.execute(new ServiceRequest<VideoResource>(videoResource));
    }

    @Override
    public ServiceResponse updateVideoResourceVersion(VideoResourceVersion videoResourceVersion) {
        ServiceRequest<VideoResourceVersion> request = new ServiceRequest<VideoResourceVersion>(videoResourceVersion);
        return updateVideoResourceVersionBroker.execute(request);
    }

    @Override
    public ServiceResponse deleteVideoResource(Long videoResourceId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(videoResourceId);
        return deleteVideoResourceBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse deleteVideoResourceVersion(Long videoResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", videoResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return deleteVideoResourceVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    DOCUMENT RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<DocumentResource> readDocumentResourceById(Long documentResourceId) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", documentResourceId);
        return readDocumentResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<DocumentResource> readDocumentResourceByIdCultureAndVersion(Long documentResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", documentResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return readDocumentResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<DocumentResource> readDocumentResourceByLookup(String lookup) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(lookup);
        return readDocumentResourceByLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<FileInfo> getDocumentResourceVersionFile(Long documentResourceId, String documentFilename) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", documentResourceId);
		namedValues.put("documentFilename", documentFilename);
        return getDocumentResourceVersionFileBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<DocumentResource> createDocumentResource(DocumentResource documentResource) {
        return saveDocumentResourceBroker.execute(new ServiceRequest<DocumentResource>(documentResource));
    }

    @Override
    public ServiceResponse<DocumentResource> createNewDocumentResourceVersions(Long documentResourceId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", documentResourceId);
        return createNewDocumentResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<DocumentResource> createNewDocumentResourceVersions(Long documentResourceId, Integer version) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", documentResourceId);
        namedValues.put("version", version);
        return createNewDocumentResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<DocumentResource> updateDocumentResource(DocumentResource documentResource) {
        return saveDocumentResourceBroker.execute(new ServiceRequest<DocumentResource>(documentResource));
    }

    @Override
    public ServiceResponse updateDocumentResourceVersion(DocumentResourceVersion documentResourceVersion) {
        ServiceRequest<DocumentResourceVersion> request = new ServiceRequest<DocumentResourceVersion>(documentResourceVersion);
        return updateDocumentResourceVersionBroker.execute(request);
    }

    @Override
    public ServiceResponse deleteDocumentResource(Long documentResourceId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(documentResourceId);
        return deleteDocumentResourceBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse deleteDocumentResourceVersion(Long documentResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", documentResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return deleteDocumentResourceVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    FILE RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<FileResource> readFileResourceById(Long fileResourceId) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", fileResourceId);
        return readFileResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<FileResource> readFileResourceByIdCultureAndVersion(Long fileResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", fileResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return readFileResourceByIdBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<FileResource> readFileResourceByLookup(String lookup) {
        SimpleIdentifier<String> simpleIdentifier = new SimpleIdentifier<String>(lookup);
        return readFileResourceByLookupBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse<FileInfo> getFileResourceVersionFile(Long fileResourceId, String fileFilename) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", fileResourceId);
		namedValues.put("fileFilename", fileFilename);
        return getFileResourceVersionFileBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<FileResource> createFileResource(FileResource fileResource) {
        return saveFileResourceBroker.execute(new ServiceRequest<FileResource>(fileResource));
    }

    @Override
    public ServiceResponse<FileResource> createNewFileResourceVersions(Long fileResourceId) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", fileResourceId);
        return createNewFileResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<FileResource> createNewFileResourceVersions(Long fileResourceId, Integer version) {
        NamedValues namedValues = new NamedValues();
        namedValues.put("resourceId", fileResourceId);
        namedValues.put("version", version);
        return createNewFileResourceVersionsBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

    @Override
    public ServiceResponse<FileResource> updateFileResource(FileResource fileResource) {
        return saveFileResourceBroker.execute(new ServiceRequest<FileResource>(fileResource));
    }

    @Override
    public ServiceResponse updateFileResourceVersion(FileResourceVersion fileResourceVersion) {
        ServiceRequest<FileResourceVersion> request = new ServiceRequest<FileResourceVersion>(fileResourceVersion);
        return updateFileResourceVersionBroker.execute(request);
    }

    @Override
    public ServiceResponse deleteFileResource(Long fileResourceId) {
        SimpleIdentifier<Long> simpleIdentifier = new SimpleIdentifier<Long>(fileResourceId);
        return deleteFileResourceBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(simpleIdentifier));
    }

    @Override
    public ServiceResponse deleteFileResourceVersion(Long fileResourceId, String culture, Integer version) {
        NamedValues namedValues = new NamedValues();
		namedValues.put("resourceId", fileResourceId);
		namedValues.put("culture", culture);
		namedValues.put("version", version);
        return deleteFileResourceVersionBroker.execute(new ServiceRequest<NamedValues>(namedValues));
    }

	@Override
	public ServiceResponse<ResourceContent> createDocumentation(ResourceContent data) {
		return createResourceDescriptionBroker.execute(new ServiceRequest<ResourceContent>(data));
	}

	@Override
	public ServiceResponse<ResourceContent> updateDocumentation(Long resourceVersionId, ResourceContent data) {
		return updateResourceDescriptionBroker.execute(new ServiceRequest<ResourceContent>(data));
	}

	@Override
	public ServiceResponse updateDocumentationImage(Long resourceVersionId, ImageResourceVersion data) {
		return updateDocumentationImageBroker.execute(new ServiceRequest<ImageResourceVersion>(data));
	}

	@Override
	public ServiceResponse deleteDocumentation(Long resourceId) {
		return deleteResourceDescriptionBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(resourceId)));
	}

	@Override
	public ServiceResponse<ActionServiceLink> readEntityServiceLinks(Long registryNodeId) {
		return readEntityServiceLinksBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(registryNodeId)));
	}

	@Override
	public ServiceResponse<ActionServiceLink> readExamplesByParentId(Long registryNodeId, String country) {
		return readEntityServiceLinksBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(registryNodeId)));
	}

	@Override
	public ServiceResponse<AbstractResource> readDescriptionsByParentId(Long nodeId, String country) {
		NamedValues values = new NamedValues();
		values.put("id", nodeId);
		values.put("country", country);
		return readDescriptionListByParentIdBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse<FieldResource> readFields(Long nodeId, String country) {
		NamedValues values = new NamedValues();
		values.put("id", nodeId);
		values.put("country", country);
		return readFieldWithResourcesListBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse<EntityResourceRelationship> readRelatedEntities(Long nodeId, String country) {
		NamedValues values = new NamedValues();
		values.put("id", nodeId);
		values.put("country", country);
		return readRelatedEntityWithResourcesListBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse<ActionResource> readActions(Long nodeId, String country) {
		NamedValues values = new NamedValues();
		values.put("id", nodeId);
		values.put("country", country);
		return readActionWithResourcesListBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse<Lookup> readChildren(Long nodeId) {
		return readRegistryNodeDocumentationChildrenListBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(nodeId)));
	}

	@Override
	public ServiceResponse<Lookup> readParents(Long nodeId) {
		return readRegistryNodeParentListBroker.execute(new ServiceRequest<SimpleIdentifier<Long>>(new SimpleIdentifier<Long>(nodeId)));
	}

	@Override
	public ServiceResponse getDocumentationHtmlSource(String lookup, String country, Integer maxImageWidth, String token) {
		NamedValues values = new NamedValues();
		values.put("lookup", lookup);
		values.put("token", token);
		values.put("country", country);
		values.put("maxImageWidth", maxImageWidth);
		return getDocumentationPageBroker.execute(new ServiceRequest<NamedValues>(values));
	}

	@Override
	public ServiceResponse generateDocumentationExample(String lookup) {
		return generateDocumentationExampleBroker.execute(new ServiceRequest<SimpleIdentifier<String>>(new SimpleIdentifier<String>(lookup)));
	}
}
