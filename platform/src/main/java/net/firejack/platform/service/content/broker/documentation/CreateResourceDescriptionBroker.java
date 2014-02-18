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

package net.firejack.platform.service.content.broker.documentation;

import net.firejack.platform.api.content.domain.ResourceContent;
import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.core.broker.ServiceBroker;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.resource.*;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.resource.ICollectionStore;
import net.firejack.platform.core.store.registry.resource.IFolderStore;
import net.firejack.platform.core.store.registry.resource.IResourceStore;
import net.firejack.platform.core.store.registry.resource.IResourceVersionStore;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.service.content.broker.documentation.service.DocumentationService;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

@TrackDetails
@Component("CreateResourceDescriptionBroker")
public class CreateResourceDescriptionBroker extends ServiceBroker<ServiceRequest<ResourceContent>, ServiceResponse<ResourceContent>> {

	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	@Autowired
	private IFolderStore folderStore;

	@Autowired
	private ICollectionStore collectionStore;

	@Autowired
	@Qualifier("resourceStore")
	private IResourceStore<AbstractResourceModel> resourceStore;

	@Autowired
	@Qualifier("resourceVersionStore")
	private IResourceVersionStore<AbstractResourceVersionModel> resourceVersionStore;

	@Override
	protected ServiceResponse<ResourceContent> perform(ServiceRequest<ResourceContent> request) throws Exception {
		ResourceContent vo = request.getData();
		String country = vo.getCountry();
		Cultures culture = Cultures.findByCountry(country);
		vo.setCulture(culture);
		String value = vo.getValue();
		Long resourceId = vo.getResourceId();
		String lookupSuffix = vo.getLookupSuffix();
		AbstractResourceVersionModel resourceVersion = null;
		if (resourceId != null) {
			AbstractResourceModel resource = resourceStore.findById(resourceId);
			if (resource instanceof TextResourceModel) {
				resourceVersion = new TextResourceVersionModel();
				((TextResourceVersionModel) resourceVersion).setText(value);
			} else if (resource instanceof HtmlResourceModel) {
				resourceVersion = new HtmlResourceVersionModel();
				((HtmlResourceVersionModel) resourceVersion).setHtml(value);
			}
			resourceVersion.setResource(resource);
			resourceVersion.setCulture(culture);
			resourceVersion.setStatus(ResourceStatus.PUBLISHED);
			resourceVersion.setUpdated(new Date());
			resourceVersion.setVersion(resource.getLastVersion());

			resourceVersionStore.saveOrUpdate(resourceVersion);
			vo.setResourceVersionId(resourceVersion.getId());

		} else if (StringUtils.isNotBlank(lookupSuffix)) {
			Long registryNodeId = vo.getRegistryNodeId();
			RegistryNodeModel registryNode = registryNodeStore.findById(registryNodeId);
			CollectionModel collection = collectionStore.findOrCreateCollection(registryNode);
			boolean needToAssociate = true;
			AbstractResourceModel resource = null;
			if (lookupSuffix.equals(DocumentationService.DESCRIPTION)) {
				ResourceType resourceType = vo.getResourceType();
				String nameWithDefault = vo.getResourceName() != null ? vo.getResourceName() : StringUtils.capitalize(DocumentationService.DESCRIPTION);
				if (ResourceType.HTML.equals(resourceType)) {
					resource = new HtmlResourceModel();
					resource.setName(nameWithDefault);
					resource.setParent(collection.getParent());
					resourceVersion = new HtmlResourceVersionModel();
					((HtmlResourceVersionModel) resourceVersion).setHtml(value);
				} else if (ResourceType.TEXT.equals(resourceType)) {
					resource = new TextResourceModel();
					resource.setName(nameWithDefault);
					resource.setParent(collection.getParent());
					resourceVersion = new TextResourceVersionModel();
					((TextResourceVersionModel) resourceVersion).setText(value);
				} else if (ResourceType.IMAGE.equals(resourceType)) {
					resource = new ImageResourceModel();
					resource.setName(nameWithDefault);
					resource.setParent(collection.getParent());
					resourceVersion = new ImageResourceVersionModel();
				}
			} else {
				resource = new TextResourceModel();
				String[] lookupSuffixs = lookupSuffix.split("\\.");
				if (lookupSuffixs.length > 1) {
					for (int i = 0; i < lookupSuffixs.length - 1; i++) {
						String searchLookup = registryNode.getLookup() + "." + StringUtils.normalize(lookupSuffixs[i]);
						RegistryNodeModel foundRegistryNode = registryNodeStore.findByLookup(searchLookup);
						if (foundRegistryNode == null) {
							FolderModel folder = new FolderModel();
							folder.setName(lookupSuffixs[i]);
							folder.setParent(registryNode);
							folderStore.save(folder);
							registryNode = folder;
						} else {
							registryNode = foundRegistryNode;
						}
					}
				}
				String name = lookupSuffixs[lookupSuffixs.length - 1];
				resource.setName(name);
				resource.setParent(registryNode);
				resourceVersion = new TextResourceVersionModel();
				((TextResourceVersionModel) resourceVersion).setText(value);
				needToAssociate = false;
			}
			resource.setLastVersion(1);
			resource.setPublishedVersion(1);
			resource.setResourceVersion(resourceVersion);

			resourceVersion.setResource(resource);
			resourceVersion.setCulture(culture);
			resourceVersion.setStatus(ResourceStatus.PUBLISHED);
			resourceVersion.setUpdated(new Date());
			resourceVersion.setVersion(resource.getLastVersion());

			resourceStore.save(resource);
			vo.setResourceId(resource.getId());
			vo.setResourceVersionId(resourceVersion.getId());
			vo.setCollectionId(collection.getId());

			if (needToAssociate) {
				collectionStore.associateCollectionWithReference(collection, resource);
			}
		}
		return new ServiceResponse<ResourceContent>(vo, "Documentation  has been created successfully.", true);
	}
}
