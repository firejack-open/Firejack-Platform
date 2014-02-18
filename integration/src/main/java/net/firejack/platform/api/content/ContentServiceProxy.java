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

import net.firejack.platform.api.AbstractServiceProxy;
import net.firejack.platform.api.content.domain.*;
import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.api.registry.domain.RegistryNodeTree;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.ListLookup;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.utils.Paging;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.utils.WebUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentServiceProxy extends AbstractServiceProxy implements IContentService {

	public ContentServiceProxy(Class[] classes) {
		super(classes);
	}

	@Override
    public String getServiceUrlSuffix() {
        return "/content";
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    FOLDER SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<Folder> readFolder(Long folderId) {
        return get("/folder/" + folderId);
    }

    @Override
    public ServiceResponse<Folder> readFoldersByParentId(Long parentId) {
        return get("/folder/list-by-parent-id/" + parentId);
    }

    @Override
    public ServiceResponse<Folder> readFoldersByLikeLookup(String lookup) {
        return get("/folder/list-by-lookup/" + lookup);
    }

    @Override
    public ServiceResponse<RegistryNodeTree> createFolder(Folder folder) {
        return post2("/folder", folder);
    }

    @Override
    public ServiceResponse<RegistryNodeTree> updateFolder(Folder folder) {
        return put2("/folder/" + folder.getId(), folder);
    }

    @Override
    public ServiceResponse deleteFolder(Long folderId) {
        return delete("/folder/" + folderId);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    COLLECTION SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<Collection> readCollection(Long collectionId) {
        return get("/collection/" + collectionId);
    }

    @Override
    public ServiceResponse<Collection> readCollectionsByParentId(Long parentId) {
        return get("/collection/list-by-parent-id/" + parentId);
    }

    @Override
    public ServiceResponse<Collection> readCollectionsByLikeLookup(String lookup) {
        return get("/collection/list-by-lookup/" + lookup);
    }

    @Override
    public ServiceResponse<RegistryNodeTree> createCollection(Collection collection) {
        return post2("/collection", collection);
    }

    @Override
    public ServiceResponse<RegistryNodeTree> updateCollection(Collection collection) {
        return put2("/collection/" + collection.getId(), collection);
    }

    @Override
    public ServiceResponse deleteCollection(Long collectionId) {
        return delete("/collection/" + collectionId);
    }

    @Override
    public ServiceResponse<FileInfo> exportCollectionArchiveFile(Long collectionId) {
        return getFile("/collection/export/" + collectionId);
    }

    @Override
    public ServiceResponse importCollectionArchiveFile(InputStream inputStream) {
        String responderData = upload("/collection/import", inputStream, null);

        ServiceResponse response;
        try {
            response = WebUtils.deserializeObjectFromJSON(responderData, ServiceResponse.class);
        } catch (IOException e) {
            throw new BusinessFunctionException(e);
        }
        return response;
    }

    @Override
    public ServiceResponse swapCollectionMemberships(Long collectionId, Long oneRefId, Long twoRefId) {
        return get("/collection/swap/" + collectionId + "/" + oneRefId + "/" + twoRefId);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<Culture> readAllCultures() {
        return get("/resource/culture");
    }

    @Override
    public ServiceResponse<Culture> readAvailableCultures(Long entityId) {
        return get("/resource/culture/" + entityId);
    }

    @Override
    public ServiceResponse<Resource> readResourcesByParentId(Long registryNodeId, List<Long> exceptIds, Paging paging) {
	    return get("/resource/node/" + registryNodeId,
			    "exceptIds", exceptIds,
			    "start", paging.getOffset(),
			    "limit", paging.getLimit(),
			    "sortColumn", paging.getSortColumn(),
			    "sortDirection", paging.getSortDirection());
    }

    @Override
    public ServiceResponse<Resource> searchResourcesByParentIdAndTerm(Long registryNodeId, String term) {
        return get("/resource/node/search/" + registryNodeId + "/" + term);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceResponse<FileInfo> uploadResourceFile(
            String resourcesType, String customTTL, InputStream inputStream, String originalFilename) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(ORIGINAL_FILENAME, originalFilename);

        StringBuilder sb = new StringBuilder("/resource/upload/");
        sb.append(resourcesType);
        if (StringUtils.isNotBlank(customTTL)) {
            sb.append("?ttl=").append(customTTL);
        }

        String responderData = upload(sb.toString(), inputStream, parameters);

        ServiceResponse<FileInfo> response;
        try {
            Class<? extends AbstractDTO> genericType = FileInfo.class;
            if (ResourceType.IMAGE.name().equals(resourcesType)) {
                genericType = ImageFileInfo.class;
            }
            response = WebUtils.deserializeObjectFromJSON(responderData, ServiceResponse.class, genericType);
        } catch (IOException e) {
            throw new BusinessFunctionException(e);
        }
        return response;
    }

    @Override
    public ServiceResponse<FileInfo> getTemporaryUploadedFile(String temporaryFilename) {
        return getFile("/resource/tmp/" + temporaryFilename);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    TEXT RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<TextResource> readTextResourceById(Long textResourceId) {
        return get("/resource/text/" + textResourceId);
    }

    @Override
    public ServiceResponse<TextResource> readTextResourceByIdCultureAndVersion(Long textResourceId, String culture, Integer version) {
        return get("/resource/text/by-id-culture-version/" + textResourceId + "/" + culture + "/" + version);
    }

    @Override
    public ServiceResponse<TextResource> readTextResourceByLookup(String lookup) {
        return get("/resource/text/by-lookup/" + lookup);
    }

    @Override
    public ServiceResponse<TextResource> createTextResource(TextResource textResource) {
        return post("/resource/text", textResource);
    }

    @Override
    public ServiceResponse<TextResource> updateTextResource(TextResource textResource) {
        return put("/resource/text/" + textResource.getId(), textResource);
    }

    @Override
    public ServiceResponse<TextResourceVersion> createTextResourceVersion(String resourceLookup, TextResourceVersion textResourceVersion) {
        return post("/resource/text/version/new/" + resourceLookup, textResourceVersion);
    }

    @Override
    public ServiceResponse<TextResourceVersion> updateTextResourceVersion(TextResourceVersion textResourceVersion) {
        return put("/resource/text/version/" + textResourceVersion.getId(), textResourceVersion);
    }

    @Override
    public ServiceResponse deleteTextResource(Long textResourceId) {
        return delete("/resource/text/" + textResourceId);
    }

    @Override
    public ServiceResponse deleteTextResourceVersion(Long textResourceId, String culture, Integer version) {
        return delete("/resource/text/version/" + textResourceId + "/" + version + "/" + culture);
    }

    @Override
    public ServiceResponse<TextResource> createNewTextResourceVersions(Long textResourceId) {
        return post("/resource/text/version/" + textResourceId);
    }

    @Override
    public ServiceResponse<TextResource> createNewTextResourceVersions(Long textResourceId, Integer version) {
        return post("/resource/text/version2/" + textResourceId + "/" + version);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    HTML RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<HtmlResource> readHtmlResourceById(Long htmlResourceId) {
        return get("/resource/html/" + htmlResourceId);
    }

    @Override
    public ServiceResponse<HtmlResource> readHtmlResourceByIdCultureAndVersion(Long htmlResourceId, String culture, Integer version) {
        return get("/resource/html/by-id-culture-version/" + htmlResourceId + "/" + culture + "/" + version);
    }

    @Override
    public ServiceResponse<HtmlResource> readHtmlResourceByLookup(String lookup) {
        return get("/resource/html/by-lookup/" + lookup);
    }

    @Override
    public ServiceResponse<HtmlResource> createHtmlResource(HtmlResource htmlResource) {
        return post("/resource/html", htmlResource);
    }

    @Override
    public ServiceResponse<HtmlResource> updateHtmlResource(HtmlResource htmlResource) {
        return put("/resource/html/" + htmlResource.getId(), htmlResource);
    }

    @Override
    public ServiceResponse updateHtmlResourceVersion(HtmlResourceVersion htmlResourceVersion) {
        return put("/resource/html/version/" + htmlResourceVersion.getId(), htmlResourceVersion);
    }

    @Override
    public ServiceResponse deleteHtmlResource(Long htmlResourceId) {
        return delete("/resource/html/" + htmlResourceId);
    }

    @Override
    public ServiceResponse deleteHtmlResourceVersion(Long htmlResourceId, String culture, Integer version) {
        return delete("/resource/html/version/" + htmlResourceId + "/" + version + "/" + culture);
    }

    @Override
    public ServiceResponse<HtmlResource> createNewHtmlResourceVersions(Long htmlResourceId) {
        return post("/resource/html/version/" + htmlResourceId);
    }

    @Override
    public ServiceResponse<HtmlResource> createNewHtmlResourceVersions(Long htmlResourceId, Integer version) {
        return post("/resource/html/version2/" + htmlResourceId + "/" + version);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    IMAGE RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<ImageResource> readImageResourceById(Long imageResourceId) {
        return get("/resource/image/" + imageResourceId);
    }

    @Override
    public ServiceResponse<ImageResource> readImageResourceByIdCultureAndVersion(Long imageResourceId, String culture, Integer version) {
        return get("/resource/image/by-id-culture-version/" + imageResourceId + "/" + culture + "/" + version);
    }

    @Override
    public ServiceResponse<AbstractResource> readResourcesByLookupList(ListLookup lookupList) {
        ServiceResponse<AbstractResource> response;
        if (lookupList == null ||lookupList.getLookup() == null ||  lookupList.getLookup().isEmpty()) {
            response = new ServiceResponse<AbstractResource>("Lookup list is empty.", false);
        } else {
            response = post3("/resource/by-lookup-list", lookupList, new Object[0]);
        }
        return response;
    }

    @Override
    public ServiceResponse<ImageResource> readImageResourceByLookup(String lookup) {
        return get("/resource/image/by-lookup/" + lookup);
    }

    @Override
    public InputStream readResourceImageStream(String lookup) {
        return getStream("/resource/image/stream/" + lookup);
    }

    @Override
    public ServiceResponse<FileInfo> getImageResourceVersionFile(Long resourceId, String imageFilename) {
        return getFile("/resource/image/by-filename/" + resourceId + "/" + imageFilename);
    }

    @Override
    public ServiceResponse<ImageResource> createImageResource(ImageResource imageResource) {
        return post("/resource/image", imageResource);
    }

    @Override
    public ServiceResponse<ImageResource> createNewImageResourceVersions(Long imageResourceId) {
        return post("/resource/image/version/" + imageResourceId);
    }

    @Override
    public ServiceResponse<ImageResource> createNewImageResourceVersions(Long imageResourceId, Integer version) {
        return post("/resource/image/version2/" + imageResourceId + "/" + version);
    }

    @Override
    public ServiceResponse<ImageResource> updateImageResource(ImageResource imageResource) {
        return put("/resource/image/" + imageResource.getId(), imageResource);
    }

    @Override
    public ServiceResponse<ImageResourceVersion> createImageResourceVersionByLookup(String resourceLookup, ImageResourceVersion imageResourceVersion) {
        return post("/resource/image/version/new-by-lookup/" + resourceLookup, imageResourceVersion);
    }

    @Override
    public ServiceResponse<ImageResourceVersion> createImageResourceVersionByPath(String resourcePath, ImageResourceVersion imageResourceVersion) {
        return post("/resource/image/version/new-by-path/" + resourcePath, imageResourceVersion);
    }

    @Override
    public ServiceResponse<ImageResourceVersion> updateImageResourceVersion(ImageResourceVersion imageResourceVersion) {
        return put("/resource/image/version/" + imageResourceVersion.getId(), imageResourceVersion);
    }

    @Override
    public ServiceResponse<ImageResourceVersion> updateImageResourceVersionByLookup(String resourceLookup, ImageResourceVersion imageResourceVersion) {
        return put("/resource/image/version/by-lookup/" + resourceLookup, imageResourceVersion);
    }

    @Override
    public ServiceResponse deleteImageResource(Long imageResourceId) {
        return delete("/resource/image/" + imageResourceId);
    }

    @Override
    public ServiceResponse deleteImageResourceByLookup(String lookup) {
        return delete("/resource/image/by-lookup/" + lookup);
    }

    @Override
    public ServiceResponse deleteImageResourceVersion(Long imageResourceId, String culture, Integer version) {
        return delete("/resource/image/version/" + imageResourceId + "/" + version + "/" + culture);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    AUDIO RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<AudioResource> readAudioResourceById(Long audioResourceId) {
        return get("/resource/audio/" + audioResourceId);
    }

    @Override
    public ServiceResponse<AudioResource> readAudioResourceByIdCultureAndVersion(Long audioResourceId, String culture, Integer version) {
        return get("/resource/audio/by-id-culture-version/" + audioResourceId + "/" + culture + "/" + version);
    }

    @Override
    public ServiceResponse<AudioResource> readAudioResourceByLookup(String lookup) {
        return get("/resource/audio/by-lookup/" + lookup);
    }

    @Override
    public ServiceResponse<FileInfo> getAudioResourceVersionFile(Long resourceId, String audioFilename) {
        return getFile("/resource/audio/by-filename/" + resourceId + "/" + audioFilename);
    }

    @Override
    public ServiceResponse<AudioResource> createAudioResource(AudioResource audioResource) {
        return post("/resource/audio", audioResource);
    }

    @Override
    public ServiceResponse<AudioResource> createNewAudioResourceVersions(Long audioResourceId) {
        return post("/resource/audio/version/" + audioResourceId);
    }

    @Override
    public ServiceResponse<AudioResource> createNewAudioResourceVersions(Long audioResourceId, Integer version) {
        return post("/resource/audio/version2/" + audioResourceId + "/" + version);
    }

    @Override
    public ServiceResponse<AudioResource> updateAudioResource(AudioResource audioResource) {
        return put("/resource/audio/" + audioResource.getId(), audioResource);
    }

    @Override
    public ServiceResponse updateAudioResourceVersion(AudioResourceVersion audioResourceVersion) {
        return put("/resource/audio/version/" + audioResourceVersion.getId(), audioResourceVersion);
    }

    @Override
    public ServiceResponse deleteAudioResource(Long audioResourceId) {
        return delete("/resource/audio/" + audioResourceId);
    }

    @Override
    public ServiceResponse deleteAudioResourceVersion(Long audioResourceId, String culture, Integer version) {
        return delete("/resource/audio/version/" + audioResourceId + "/" + version + "/" + culture);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    VIDEO RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<VideoResource> readVideoResourceById(Long videoResourceId) {
        return get("/resource/video/" + videoResourceId);
    }

    @Override
    public ServiceResponse<VideoResource> readVideoResourceByIdCultureAndVersion(Long videoResourceId, String culture, Integer version) {
        return get("/resource/video/by-id-culture-version/" + videoResourceId + "/" + culture + "/" + version);
    }

    @Override
    public ServiceResponse<VideoResource> readVideoResourceByLookup(String lookup) {
        return get("/resource/video/by-lookup/" + lookup);
    }

    @Override
    public ServiceResponse<FileInfo> getVideoResourceVersionFile(Long resourceId, String videoFilename) {
        return getFile("/resource/video/by-filename/" + resourceId + "/" + videoFilename);
    }

    @Override
    public ServiceResponse<VideoResource> createVideoResource(VideoResource videoResource) {
        return post("/resource/video", videoResource);
    }

    @Override
    public ServiceResponse<VideoResource> createNewVideoResourceVersions(Long videoResourceId) {
        return post("/resource/video/version/" + videoResourceId);
    }

    @Override
    public ServiceResponse<VideoResource> createNewVideoResourceVersions(Long videoResourceId, Integer version) {
        return post("/resource/video/version2/" + videoResourceId + "/" + version);
    }

    @Override
    public ServiceResponse<VideoResource> updateVideoResource(VideoResource videoResource) {
        return put("/resource/video/" + videoResource.getId(), videoResource);
    }

    @Override
    public ServiceResponse updateVideoResourceVersion(VideoResourceVersion videoResourceVersion) {
        return put("/resource/video/version/" + videoResourceVersion.getId(), videoResourceVersion);
    }

    @Override
    public ServiceResponse deleteVideoResource(Long videoResourceId) {
        return delete("/resource/video/" + videoResourceId);
    }

    @Override
    public ServiceResponse deleteVideoResourceVersion(Long videoResourceId, String culture, Integer version) {
        return delete("/resource/video/version/" + videoResourceId + "/" + version + "/" + culture);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    DOCUMENT RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<DocumentResource> readDocumentResourceById(Long documentResourceId) {
        return get("/resource/document/" + documentResourceId);
    }

    @Override
    public ServiceResponse<DocumentResource> readDocumentResourceByIdCultureAndVersion(Long documentResourceId, String culture, Integer version) {
        return get("/resource/document/by-id-culture-version/" + documentResourceId + "/" + culture + "/" + version);
    }

    @Override
    public ServiceResponse<DocumentResource> readDocumentResourceByLookup(String lookup) {
        return get("/resource/document/by-lookup/" + lookup);
    }

    @Override
    public ServiceResponse<FileInfo> getDocumentResourceVersionFile(Long resourceId, String documentFilename) {
        return getFile("/resource/document/by-filename/" + resourceId + "/" + documentFilename);
    }

    @Override
    public ServiceResponse<DocumentResource> createDocumentResource(DocumentResource documentResource) {
        return post("/resource/document", documentResource);
    }

    @Override
    public ServiceResponse<DocumentResource> createNewDocumentResourceVersions(Long documentResourceId) {
        return post("/resource/document/version/" + documentResourceId);
    }

    @Override
    public ServiceResponse<DocumentResource> createNewDocumentResourceVersions(Long documentResourceId, Integer version) {
        return post("/resource/document/version2/" + documentResourceId + "/" + version);
    }

    @Override
    public ServiceResponse<DocumentResource> updateDocumentResource(DocumentResource documentResource) {
        return put("/resource/document/" + documentResource.getId(), documentResource);
    }

    @Override
    public ServiceResponse updateDocumentResourceVersion(DocumentResourceVersion documentResourceVersion) {
        return put("/resource/document/version/" + documentResourceVersion.getId(), documentResourceVersion);
    }

    @Override
    public ServiceResponse deleteDocumentResource(Long documentResourceId) {
        return delete("/resource/document/" + documentResourceId);
    }

    @Override
    public ServiceResponse deleteDocumentResourceVersion(Long documentResourceId, String culture, Integer version) {
        return delete("/resource/document/version/" + documentResourceId + "/" + version + "/" + culture);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    FILE RESOURCE SERVICES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ServiceResponse<FileResource> readFileResourceById(Long fileResourceId) {
        return get("/resource/file/" + fileResourceId);
    }

    @Override
    public ServiceResponse<FileResource> readFileResourceByIdCultureAndVersion(Long fileResourceId, String culture, Integer version) {
        return get("/resource/file/by-id-culture-version/" + fileResourceId + "/" + culture + "/" + version);
    }

    @Override
    public ServiceResponse<FileResource> readFileResourceByLookup(String lookup) {
        return get("/resource/file/by-lookup/" + lookup);
    }

    @Override
    public ServiceResponse<FileInfo> getFileResourceVersionFile(Long resourceId, String fileFilename) {
        return getFile("/resource/file/by-filename/" + resourceId + "/" + fileFilename);
    }

    @Override
    public ServiceResponse<FileResource> createFileResource(FileResource fileResource) {
        return post("/resource/file", fileResource);
    }

    @Override
    public ServiceResponse<FileResource> createNewFileResourceVersions(Long fileResourceId) {
        return post("/resource/file/version/" + fileResourceId);
    }

    @Override
    public ServiceResponse<FileResource> createNewFileResourceVersions(Long fileResourceId, Integer version) {
        return post("/resource/file/version2/" + fileResourceId + "/" + version);
    }

    @Override
    public ServiceResponse<FileResource> updateFileResource(FileResource fileResource) {
        return put("/resource/file/" + fileResource.getId(), fileResource);
    }

    @Override
    public ServiceResponse updateFileResourceVersion(FileResourceVersion fileResourceVersion) {
        return put("/resource/file/version/" + fileResourceVersion.getId(), fileResourceVersion);
    }

    @Override
    public ServiceResponse deleteFileResource(Long fileResourceId) {
        return delete("/resource/file/" + fileResourceId);
    }

    @Override
    public ServiceResponse deleteFileResourceVersion(Long fileResourceId, String culture, Integer version) {
        return delete("/resource/file/version/" + fileResourceId + "/" + version + "/" + culture);
    }

	@Override
	public ServiceResponse<ResourceContent> createDocumentation(ResourceContent data) {
		return post2("/documentation", data);
	}

	@Override
	public ServiceResponse<ResourceContent> updateDocumentation(Long resourceVersionId, ResourceContent data) {
		return put2("/documentation/" + resourceVersionId, data);
	}

	@Override
	public ServiceResponse updateDocumentationImage(Long resourceVersionId, ImageResourceVersion data) {
		return put2("/documentation/image/" + resourceVersionId, data);
	}

	@Override
	public ServiceResponse deleteDocumentation(Long resourceId) {
		return delete("/documentation/" + resourceId);
	}

	@Override
	public ServiceResponse<ActionServiceLink> readEntityServiceLinks(Long registryNodeId) {
		return get("/documentation/links/" + registryNodeId);
	}

	@Override
	public ServiceResponse<ActionServiceLink> readExamplesByParentId(Long registryNodeId, String country) {
		return get("/documentation/examples/" + registryNodeId, "country", country);
	}

	@Override
	public ServiceResponse<AbstractResource> readDescriptionsByParentId(Long nodeId, String country) {
		return get("/documentation/descriptions/" + nodeId, "country", country);
	}

	@Override
	public ServiceResponse<FieldResource> readFields(Long nodeId, String country) {
		return get("/documentation/fields/" + nodeId, "country", country);
	}

	@Override
	public ServiceResponse<EntityResourceRelationship> readRelatedEntities(Long nodeId, String country) {
		return get("/documentation/entities/" + nodeId, "country", country);
	}

	@Override
	public ServiceResponse<ActionResource> readActions(Long nodeId, String country) {
		return get("/documentation/actions/" + nodeId, "country", country);
	}

	@Override
	public ServiceResponse<Lookup> readChildren(Long nodeId) {
		return get("/documentation/children/" + nodeId);
	}

	@Override
	public ServiceResponse<Lookup> readParents(Long nodeId) {
		return get("/documentation/parents/" + nodeId);
	}

	@Override
	public ServiceResponse getDocumentationHtmlSource(String lookup, String country, Integer maxImageWidth, String token) {
		return get("/documentation/html/" + country + "/" + lookup, "maxImageWidth", maxImageWidth);
	}

	@Override
	public ServiceResponse generateDocumentationExample(String lookup) {
		return put("/documentation/generate/example/" + lookup);
	}
}
