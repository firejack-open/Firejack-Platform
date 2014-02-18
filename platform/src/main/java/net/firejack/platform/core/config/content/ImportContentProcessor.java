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

package net.firejack.platform.core.config.content;


import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.core.config.meta.construct.ContentDescriptor;
import net.firejack.platform.core.config.meta.element.FolderElement;
import net.firejack.platform.core.config.meta.element.resource.*;
import net.firejack.platform.core.config.meta.factory.CollectionElementFactory;
import net.firejack.platform.core.config.meta.factory.FolderElementFactory;
import net.firejack.platform.core.config.meta.factory.ResourceElementFactory;
import net.firejack.platform.core.config.meta.factory.ResourceVersionElementFactory;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.resource.*;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import net.firejack.platform.core.store.registry.resource.IFolderStore;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.model.helper.FileHelper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ImportContentProcessor {

    @Autowired
	private FileHelper helper;

    @Autowired
    @Qualifier("collectionStore")
    private ICollectionStore collectionStore;

    @Autowired
    @Qualifier("folderStore")
    private IFolderStore folderStore;

    @Resource(name = "registryNodeStories")
    private Map<RegistryNodeType, IRegistryNodeStore<LookupModel>> registryNodeStories;

    @Autowired
    @Qualifier("folderElementFactory")
    private FolderElementFactory folderElementFactory;

    @Autowired
    @Qualifier("resourceElementFactory")
    private ResourceElementFactory resourceElementFactory;

    @Autowired
    @Qualifier("resourceVersionElementFactory")
    private ResourceVersionElementFactory resourceVersionElementFactory;

    @Autowired
    @Qualifier("collectionElementFactory")
    private CollectionElementFactory collectionElementFactory;


    @Transactional
    public void importContent(InputStream  contentXmlFile, InputStream resourceZipFile) throws JAXBException, IOException {
	    ContentDescriptor contentDescriptor = FileUtils.readJAXB(ContentDescriptor.class, contentXmlFile);

        List<FolderElement> folderElements = contentDescriptor.getFolderElements();
        List<FolderModel> folders = folderElementFactory.getEntityList(folderElements);
        for (FolderModel folder : folders) {
            FolderModel foundFolder = folderStore.findByLookup(folder.getLookup());
            if (foundFolder != null) {
                foundFolder.setDescription(folder.getDescription());
                folder = foundFolder;
            }
            if (folder.getParent() != null) {
                folderStore.save(folder);
            } else {
                throw new BusinessFunctionException("Can't create FOLDER [lookup: " + folder.getLookup() + "] without parent.");
            }
        }

	    if (resourceZipFile != null) {
		    OPFEngine.FileStoreService.unzip(OpenFlame.FILESTORE_BASE, resourceZipFile, helper.getTemp());
	    }

	    List<ResourceElement> resourceElements = contentDescriptor.getResourceElements();
        if (resourceElements != null) {
            for (ResourceElement resourceElement : resourceElements) {
                ResourceModel resource = resourceElementFactory.getEntity(resourceElement);
                ResourceVersionElement[] resourceVersionElements = resourceElementFactory.getRNResourceVersions(resourceElement);
                List<AbstractResourceVersionModel<ResourceModel>> resourceVersions = new ArrayList<AbstractResourceVersionModel<ResourceModel>>();
                for (ResourceVersionElement resourceVersionElement : resourceVersionElements) {
                    AbstractResourceVersionModel resourceVersion = resourceVersionElementFactory.getResourceVersion(resourceVersionElement, resource);
                    resourceVersions.add(resourceVersion);
                }
                IResourceStore<ResourceModel> resourceStore = (IResourceStore) registryNodeStories.get(resource.getType());
                resourceVersions = resourceStore.save(resource, resourceVersions);

                for (int i = 0, resourceVersionsSize = resourceVersions.size(); i < resourceVersionsSize; i++) {
                    AbstractResourceVersionModel resourceVersion = resourceVersions.get(i);
                    ResourceVersionElement resourceVersionElement = resourceVersionElements[i];
                    saveResourceVersionFile(resourceVersion, resourceVersionElement);
                }
            }
        }

        List<CollectionElement> collectionElements = contentDescriptor.getCollectionElements();
        collectionElementSorter(collectionElements);

        for (CollectionElement collectionElement : collectionElements) {
            CollectionModel collection = collectionElementFactory.getEntity(collectionElement);
            CollectionModel foundCollection = collectionStore.findByLookup(collection.getLookup());
            if (foundCollection == null) {
                if (collection.getParent() == null) {
                    throw new BusinessFunctionException("Can't create COLLECTION [lookup: " + collection.getLookup() + "] without parent.");
                } else {
                    collectionStore.save(collection, false);
                }
            } else {
                collection.setId(foundCollection.getId());
                collection.setUid(foundCollection.getUid());
                collection.setHash(foundCollection.getHash());
                if (collection.getCollectionMembers() == null) {
                    collection.setCollectionMembers(Collections.<CollectionMemberModel>emptyList());
                }
                collectionStore.save(collection, false);
            }
        }
    }

    private void saveResourceVersionFile(AbstractResourceVersionModel resourceVersion, ResourceVersionElement resourceVersionElement) {
        if (resourceVersion instanceof IStorableResourceVersionModel) {
            String storedFileName = resourceVersion.getResourceStoredFileName();
            String resourcePath = String.valueOf(resourceVersion.getResource().getId());

	        String resourcePathFolder = null;
	        if (resourceVersion instanceof ImageResourceVersionModel) {
		        resourcePathFolder = helper.getImage();
	        } else if (resourceVersion instanceof AudioResourceVersionModel) {
		        resourcePathFolder = helper.getAudio();
	        } else if (resourceVersion instanceof VideoResourceVersionModel) {
		        resourcePathFolder = helper.getVideo();
	        }
	        if (resourcePathFolder != null) {
		        String resourceFilename = ((IStorableResourceVersionDescriptorElement) resourceVersionElement).getResourceFilename();
		        InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE,resourceFilename, helper.getTemp());
		        OPFEngine.FileStoreService.upload(OpenFlame.FILESTORE_CONTENT, storedFileName, stream, resourcePathFolder, resourcePath);
		        IOUtils.closeQuietly(stream);
	        }
        }
    }

    private void collectionElementSorter(List<CollectionElement> collectionElements) {
        for (int i = 0; i < collectionElements.size(); i++) {
            CollectionElement collectionElement = collectionElements.get(i);
            List<CollectionMemberElement> collectionMembers = collectionElement.getCollectionMembers();
            if (collectionMembers != null && !collectionMembers.isEmpty()) {
                for (CollectionMemberElement collectionMemberElement : collectionMembers) {
                    Integer position = getCollectionPosition(collectionElements, collectionMemberElement.getReference());
                    if (position != null && position > i) {
                        CollectionElement movableElement = collectionElements.get(position);
                        collectionElements.remove(movableElement);
                        collectionElements.add(i, movableElement);

                        collectionElementSorter(collectionElements);
                        return;
                    }
                }
            }
        }
    }

    private Integer getCollectionPosition(List<CollectionElement> collectionElements, String lookup) {
        for (int i = 0; i < collectionElements.size(); i++) {
            CollectionElement collectionElement = collectionElements.get(i);
            String collectionElementLookup = DiffUtils.lookup(collectionElement.getPath(), collectionElement.getName());
            if (collectionElementLookup.equals(lookup)) {
                return i;
            }
        }
        return null;
    }

}
