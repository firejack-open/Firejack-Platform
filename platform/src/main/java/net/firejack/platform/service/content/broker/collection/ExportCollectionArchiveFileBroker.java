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

package net.firejack.platform.service.content.broker.collection;

import net.firejack.platform.api.OPFEngine;
import net.firejack.platform.api.content.domain.FileInfo;
import net.firejack.platform.api.registry.model.PackageFileType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.config.export.GeneratePackageXmlHelper;
import net.firejack.platform.core.config.meta.construct.ContentDescriptor;
import net.firejack.platform.core.config.meta.element.FolderElement;
import net.firejack.platform.core.config.meta.element.resource.CollectionElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceVersionElement;
import net.firejack.platform.core.config.meta.factory.CollectionElementFactory;
import net.firejack.platform.core.config.meta.factory.FolderElementFactory;
import net.firejack.platform.core.config.meta.factory.ResourceElementFactory;
import net.firejack.platform.core.config.meta.factory.ResourceVersionElementFactory;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.resource.AbstractResourceVersionModel;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.model.registry.resource.FolderModel;
import net.firejack.platform.core.model.registry.resource.ResourceModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.FileUtils;
import net.firejack.platform.core.utils.OpenFlame;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.helper.FileHelper;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;


@Component("exportCollectionArchiveFileBroker")
@TrackDetails
public class ExportCollectionArchiveFileBroker
		extends ServiceBroker<ServiceRequest<SimpleIdentifier<Long>>, ServiceResponse<FileInfo>> {

	@Autowired
	private FileHelper helper;

	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	@Autowired
	@Qualifier("collectionStore")
	private ICollectionStore collectionStore;

	@Autowired
	@Qualifier("resourceVersionStore")
	private IResourceVersionStore<AbstractResourceVersionModel> resourceVersionStore;

	@Autowired
	@Qualifier("folderElementFactory")
	private FolderElementFactory folderElementFactory;

	@Autowired
	@Qualifier("collectionElementFactory")
	private CollectionElementFactory collectionElementFactory;

	@Autowired
	@Qualifier("resourceElementFactory")
	private ResourceElementFactory resourceElementFactory;

	@Autowired
	@Qualifier("resourceVersionElementFactory")
	private ResourceVersionElementFactory resourceVersionElementFactory;

	@Autowired
	@Qualifier("generatePackageXmlHelper")
	private GeneratePackageXmlHelper generatePackageXmlHelper;


	@Override
	protected ServiceResponse<FileInfo> perform(ServiceRequest<SimpleIdentifier<Long>> request) throws Exception {
		Long collectionId = request.getData().getIdentifier();

		Map<String, InputStream> filePaths = new HashMap<String, InputStream>();
		Long uploadFileTime = new Date().getTime();

		ServiceResponse<FileInfo> response = new ServiceResponse<FileInfo>();

		CollectionModel collection = collectionStore.findById(collectionId);
		List<FolderModel> folders = new ArrayList<FolderModel>();
		List<CollectionModel> collections = new ArrayList<CollectionModel>();
		List<ResourceModel> resources = new ArrayList<ResourceModel>();
		findReferences(collection, collections, resources);

		for (CollectionModel col : collections) {
			RegistryNodeModel parent = registryNodeStore.findById(col.getParent().getId());
			if (parent instanceof FolderModel) {
				folders.add((FolderModel) parent);
			}
		}

		List<ResourceElement> resourceElements = new ArrayList<ResourceElement>();
		List<AbstractResourceVersionModel> resourceVersions = new ArrayList<AbstractResourceVersionModel>();
		List<ResourceVersionElement> resourceVersionElements = new ArrayList<ResourceVersionElement>();

		for (ResourceModel resource : resources) {
			ResourceElement resourceElement = resourceElementFactory.getDescriptorElement(resource);
			resourceElement.setResourceType(resource.getResourceType());
			List<AbstractResourceVersionModel> versions = resourceVersionStore.findLastVersionsByResourceId(resource.getId());
			for (AbstractResourceVersionModel resourceVersion : versions) {
				ResourceVersionElement resourceVersionElement = resourceVersionElementFactory.getResourceVersionElement(resourceVersion);
				resourceVersionElementFactory.addTypedResourceVersion(resourceElement, resourceVersionElement);
				resourceVersions.add(resourceVersion);
				resourceVersionElements.add(resourceVersionElement);
			}
			resourceElements.add(resourceElement);

			RegistryNodeModel parent = registryNodeStore.findById(resource.getParent().getId());
			if (parent instanceof FolderModel) {
				folders.add((FolderModel) parent);
			}
		}

		String generatePackageResourceFilePath = generatePackageXmlHelper.generateResourcesZipFile(resourceVersions, resourceVersionElements);
		if (StringUtils.isNotBlank(generatePackageResourceFilePath)) {
			InputStream stream = OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE, generatePackageResourceFilePath, helper.getTemp());
			filePaths.put(PackageFileType.RESOURCE_ZIP.getOfrFileName(), stream);
		}

		List<FolderElement> folderElements = folderElementFactory.getDescriptorElementList(folders);
		folderElements = new ArrayList<FolderElement>(new HashSet<FolderElement>(folderElements));
		List<CollectionElement> collectionElements = collectionElementFactory.getDescriptorElementList(collections);
		ContentDescriptor contentDescriptor = new ContentDescriptor(folderElements, collectionElements, resourceElements);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		FileUtils.writeJAXB(contentDescriptor, stream);
		filePaths.put(PackageFileType.CONTENT_XML.getOfrFileName(), new ByteArrayInputStream(stream.toByteArray()));

		String temporaryContentArchiveFilename = SecurityHelper.generateRandomSequence(16) + "." + uploadFileTime;

		OPFEngine.FileStoreService.zip(OpenFlame.FILESTORE_BASE,filePaths, helper.getTemp(), temporaryContentArchiveFilename);

		FileInfo fileInfo = new FileInfo();
		fileInfo.setStream(OPFEngine.FileStoreService.download(OpenFlame.FILESTORE_BASE,temporaryContentArchiveFilename,helper.getTemp()));
		fileInfo.setFilename(collection.getName() + PackageFileType.CONTENT_ARCHIVE.getDotExtension());
		response.addItem(fileInfo);
		response.setSuccess(true);

		return response;
	}

	/**
	 * Collection with found references: resources and collections
	 *
	 * @param collection
	 * @param collections
	 * @param resources
	 */
	public void findReferences(CollectionModel collection, List<CollectionModel> collections, List<ResourceModel> resources) {
		collections.add(collection);
		List<RegistryNodeModel> references = collectionStore.findReferences(collection.getId());
		for (RegistryNodeModel reference : references) {
			if (reference instanceof ResourceModel) {
				resources.add((ResourceModel) reference);
			} else if (reference.getType() == RegistryNodeType.COLLECTION) {
				CollectionModel childCollection = collectionStore.findById(reference.getId());
				findReferences(childCollection, collections, resources);
			}
		}
	}

}
