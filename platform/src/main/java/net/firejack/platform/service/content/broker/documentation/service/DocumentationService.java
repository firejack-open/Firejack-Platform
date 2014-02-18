/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.service.content.broker.documentation.service;

import net.firejack.platform.api.content.domain.ActionServiceLink;
import net.firejack.platform.core.model.registry.FieldContainerRegistryNode;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import net.firejack.platform.core.model.registry.domain.*;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.resource.*;
import net.firejack.platform.core.store.registry.*;
import net.firejack.platform.core.store.registry.resource.*;
import net.firejack.platform.core.utils.IHasName;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.service.DocumentationLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component("documentationService")
public class DocumentationService {

	public static final String FIELDS = "fields";
    public static final String PARAMETERS = "parameters";
	public static final String RELATIONSHIPS = "relationships";
	public static final String ACTIONS = "actions";
	public static final String DESCRIPTION = "description";
	public static final String AUTO_DESCRIPTION = "auto-description";

	@Autowired
	@Qualifier("registryNodeStore")
	private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

	@Autowired
	@Qualifier("rootDomainStore")
	private IRootDomainStore rootDomainStore;

	@Autowired
	@Qualifier("folderStore")
	private IFolderStore folderStore;

	@Autowired
	@Qualifier("collectionStore")
	private ICollectionStore collectionStore;

	@Autowired
	@Qualifier("resourceStore")
	private IResourceStore<ResourceModel> resourceStore;

	@Autowired
	@Qualifier("resourceVersionStore")
	private IResourceVersionStore<AbstractResourceVersionModel<ResourceModel>> resourceVersionStore;

	@Autowired
	@Qualifier("fieldableStore")
	private IFieldContainerStore<EntityModel> fieldableStore;

	@Autowired
	private IActionParameterStore actionParameterStore;

	@Autowired
	@Qualifier("relationshipStore")
	private IRelationshipStore relationshipStore;

	@Autowired
	@Qualifier("packageStore")
	private IPackageStore packageStore;

	@Autowired
	@Qualifier("actionStore")
	private IActionStore actionStore;

	@Autowired
	@Qualifier("documentationLinkService")
	private DocumentationLinkService documentationLinkService;

	private static List<String> TREE_DOC_TYPES = RegistryNodeType.searchable();

	/**
	 * @param collectionId
	 *
	 * @return
	 */
	public List<Cultures> findAvailableCultures(Long collectionId) {
		return resourceStore.findAvailableCulturesByCollectionId(collectionId);
	}

	/**
	 * @param registryNodeId
	 *
	 * @return
	 */
	public List<RegistryNodeModel> findParentRegistryNodes(Long registryNodeId) {
		List<Long> registryNodeIds = new ArrayList<Long>();
		List<Object[]> collectionArrayIds = registryNodeStore.findAllIdAndParentId();
		registryNodeStore.findCollectionParentIds(registryNodeIds, registryNodeId, collectionArrayIds);
		List<RegistryNodeModel> registryNodes = registryNodeStore.findByIdsWithFilter(registryNodeIds, null);
		List<RegistryNodeModel> filteredRegistryNodes = new ArrayList<RegistryNodeModel>();
		for (RegistryNodeModel registryNode : registryNodes) {
			if (registryNode.getType().isSearchable()) {
				filteredRegistryNodes.add(registryNode);
			}
		}
		return filteredRegistryNodes;
	}

	/**
	 * Find all root domains
	 *
	 * @return list of root domains
	 */
	public List<RootDomainModel> findRootDomains() {
		return rootDomainStore.findAll();
	}

	/**
	 * @param registryNodeId
	 *
	 * @return
	 */
	public List<RegistryNodeModel> findChildrenRegistryNodes(Long registryNodeId) {
		return registryNodeStore.findChildrenByParentIdAndTypes(registryNodeId, TREE_DOC_TYPES, null);
	}

	/**
	 * @param actionId
	 *
	 * @return
	 */
	public List<ActionServiceLink> findLinks(Long actionId) {
		List<ActionServiceLink> actionServiceLinkVOs = new ArrayList<ActionServiceLink>();
		ActionModel action = actionStore.findById(actionId);
		if (action != null) {
			String baseUrl = null;
			PackageModel packageRN = packageStore.findWithSystemByChildrenId(actionId);
			if (packageRN != null && packageRN.getSystem() != null) {
				baseUrl = packageRN.constructServerUrl();
			}
			ActionServiceLink wsdlLinkVO = new ActionServiceLink(baseUrl);
			wsdlLinkVO.setType("WSDL");
			wsdlLinkVO.setUrl(action.getSoapUrlPath() + "?wsdl");
			actionServiceLinkVOs.add(wsdlLinkVO);

			ActionServiceLink wadlLinkVO = new ActionServiceLink(baseUrl);
			wadlLinkVO.setType("WADL");
			wadlLinkVO.setUrl("/rest/application.wadl");
			actionServiceLinkVOs.add(wadlLinkVO);

			String restUrl = documentationLinkService.generateRestUrl(action);
			ActionServiceLink restLinkVO = new ActionServiceLink(baseUrl);
			restLinkVO.setType("REST");
			restLinkVO.setUrl(restUrl);
			restLinkVO.setMethod(action.getMethod().name());
			actionServiceLinkVOs.add(restLinkVO);

			ActionServiceLink soapLinkVO = new ActionServiceLink(baseUrl);
			soapLinkVO.setType("SOAP");
			soapLinkVO.setUrl(action.getSoapUrlPath());
			soapLinkVO.setMethod(action.getSoapMethod());
			actionServiceLinkVOs.add(soapLinkVO);
		}
		return actionServiceLinkVOs;
	}

	/**
	 * @param collection
	 * @param culture
	 *
	 * @return Collection with found references: resources and collections
	 */
	public ReferenceWrapper findReferences(CollectionModel collection, Cultures culture) {
		ReferenceWrapper referenceWrapper = new ReferenceWrapper(collection);
		List<ReferenceWrapper> referenceWrappers = findReferences(referenceWrapper, culture);
		referenceWrapper.setChildrenReferences(referenceWrappers);
		return referenceWrapper;
	}

	/**
	 * Find recurring references
	 *
	 * @param mainReferenceWrapper
	 * @param culture
	 *
	 * @return list of ReferenceWrapper
	 */
	private List<ReferenceWrapper> findReferences(ReferenceWrapper mainReferenceWrapper, Cultures culture) {
		List<ReferenceWrapper> referenceWrappers = new ArrayList<ReferenceWrapper>();
		List<RegistryNodeModel> references = collectionStore.findReferences(mainReferenceWrapper.getReference().getId());
		for (RegistryNodeModel reference : references) {
			ReferenceWrapper referenceWrapper = new ReferenceWrapper(reference);
			if (reference.getType() == RegistryNodeType.HTML_RESOURCE
					|| reference.getType() == RegistryNodeType.TEXT_RESOURCE
					|| reference.getType() == RegistryNodeType.IMAGE_RESOURCE) {
				ResourceModel resource = (ResourceModel) reference;
				AbstractResourceVersionModel<ResourceModel> resourceVersion = findResourceVersion(culture, resource);
				resource.setResourceVersion(resourceVersion);
			} else if (reference.getType() == RegistryNodeType.COLLECTION) {
				List<ReferenceWrapper> childrenReferences = findReferences(referenceWrapper, culture);
				referenceWrapper.setChildrenReferences(childrenReferences);
			}
			referenceWrappers.add(referenceWrapper);
		}
		return referenceWrappers;
	}

	/**
	 * @param registryNodeId
	 * @param culture
	 *
	 * @return
	 */
	public List<Property<FieldModel>> findProperties(Long registryNodeId, Cultures culture) {
		RegistryNodeModel registryNode = fieldableStore.findById(registryNodeId);
		List<Property<FieldModel>> properties = new ArrayList<Property<FieldModel>>();
		if (registryNode != null) {
			List<FieldModel> fields = ((FieldContainerRegistryNode) registryNode).getFields();
			properties = loadResources(culture, registryNode, fields, FIELDS);
		}
		return properties;
	}

	public List<Property<ActionParameterModel>> findParameterProperties(RegistryNodeModel registryNode, Cultures culture) {
		List<Property<ActionParameterModel>> parameters = new ArrayList<Property<ActionParameterModel>>();
		if (registryNode != null && registryNode.getType() == RegistryNodeType.ACTION) {
			ActionModel action = (ActionModel) registryNode;
			if (action.getInputVOEntity() != null) {
				EntityModel entityModel = fieldableStore.findById(action.getInputVOEntity().getId());
				ActionParameterModel model = new ActionParameterModel();
				model.setName("data");
				model.setDescription(entityModel.getDescription());
				Property<ActionParameterModel> property = new Property<ActionParameterModel>(model);
				property.setData(entityModel);
                ResourceModel resource = resourceStore.findByLookup(entityModel.getLookup() + "." + FolderStore.DEFAULT_DOC_FOLDER_NAME + "." + DESCRIPTION);
                if (resource != null) {
                    AbstractResourceVersionModel<ResourceModel> resourceVersion = findResourceVersion(culture, resource);
                    resource.setResourceVersion(resourceVersion);
                    property.setResource(resource);
                }
				parameters.add(property);
			}

			List<ActionParameterModel> actionParameters = actionParameterStore.findAllByActionId(registryNode.getId());
			if (actionParameters != null) {
				parameters.addAll(loadResources(culture, action, actionParameters, PARAMETERS));
			}
		}
		return parameters;
	}

	/**
	 * @param registryNodeId
	 * @param culture
	 *
	 * @return
	 */
	public List<RelatedEntityProperty> findRelatedEntities(Long registryNodeId, Cultures culture) {
		List<RelationshipModel> relationships = relationshipStore.findRelatedEntitiesByEntityId(registryNodeId, null);
		List<RelatedEntityProperty> relatedEntityProperties = new ArrayList<RelatedEntityProperty>();
		Set<EntityModel> uniqueEntities = new HashSet<EntityModel>();
		for (RelationshipModel relationship : relationships) {
			EntityModel sourceEntity = relationship.getSourceEntity();
			EntityModel targetEntity = relationship.getTargetEntity();
			EntityModel entity = null;
			if (sourceEntity.getId().equals(registryNodeId)) {
				entity = targetEntity;
			} else if (targetEntity.getId().equals(registryNodeId)) {
				entity = sourceEntity;
			}
			if (entity != null && !uniqueEntities.contains(entity)) {
				uniqueEntities.add(entity);
				RelatedEntityProperty relatedEntityProperty = new RelatedEntityProperty(entity);
				relatedEntityProperty.setRelationship(relationship);
				ResourceModel resource = resourceStore.findByLookup(entity.getLookup() + "." + FolderStore.DEFAULT_DOC_FOLDER_NAME + "." + DESCRIPTION);
				if (resource != null) {
					AbstractResourceVersionModel<ResourceModel> resourceVersion = findResourceVersion(culture, resource);
					resource.setResourceVersion(resourceVersion);
					relatedEntityProperty.setResource(resource);
				}
				relatedEntityProperties.add(relatedEntityProperty);
			}
		}
		return relatedEntityProperties;
	}

    /**
     * @param registryNode
     * @param culture
     * @return
     */
    public List<Property<ActionModel>> findActions(RegistryNodeModel registryNode, Cultures culture) {
        List<Property<ActionModel>> properties = new ArrayList<Property<ActionModel>>();
        List<ActionModel> actions = actionStore.findChildrenByParentId(registryNode.getId(), null);
        for (ActionModel action : actions) {
            Property<ActionModel> property = new Property<ActionModel>(action);
            ResourceModel resource = resourceStore.findByLookup(action.getLookup() + "." + FolderStore.DEFAULT_DOC_FOLDER_NAME + "." + DESCRIPTION);
            if (resource != null) {
                AbstractResourceVersionModel<ResourceModel> resourceVersion = findResourceVersion(culture, resource);
                resource.setResourceVersion(resourceVersion);
                property.setResource(resource);
            }
            properties.add(property);
        }
        return properties;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private AbstractResourceVersionModel<ResourceModel> findResourceVersion(Cultures culture, ResourceModel resource) {
		return resourceVersionStore.findLastVersionByResourceIdCulture(resource.getId(), culture);
	}

	private <E extends IHasName> List<Property<E>> loadResources(Cultures culture, RegistryNodeModel registryNode, List<E> entities, String folderName) {
		List<Property<E>> properties = new ArrayList<Property<E>>();
		FolderModel fieldsFolder = folderStore.findByLookup(registryNode.getLookup() + "." + folderName);
		List<ResourceModel> resources = null;
		if (fieldsFolder != null) {
			resources = resourceStore.findChildrenByParentIdAndTypes(fieldsFolder.getId(), null, TextResourceModel.class);
		}
		for (E entity : entities) {
			Property<E> property = new Property<E>(entity);
			if (resources != null) {
				for (ResourceModel resource : resources) {
					if (StringUtils.normalize(entity.getName()).equals(StringUtils.normalize(resource.getName()))) {
						AbstractResourceVersionModel<ResourceModel> resourceVersion = findResourceVersion(culture, resource);
						resource.setResourceVersion(resourceVersion);
						property.setResource(resource);
						break;
					}
				}
			}
			properties.add(property);
		}
		return properties;
	}

}
