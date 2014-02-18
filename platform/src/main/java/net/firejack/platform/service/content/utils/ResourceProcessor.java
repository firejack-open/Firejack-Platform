/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.service.content.utils;

import net.firejack.platform.api.content.domain.ResourceContent;
import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.domain.ActionParameterModel;
import net.firejack.platform.core.model.registry.field.FieldModel;
import net.firejack.platform.core.model.registry.resource.*;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.registry.resource.*;
import net.firejack.platform.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("resourceProcessor")
public class ResourceProcessor {

    public static final String DESCRIPTION = "description";

    @Autowired
    @Qualifier("registryNodeStore")
    private IRegistryNodeStore<RegistryNodeModel> registryNodeStore;

    @Autowired
    @Qualifier("folderStore")
    private IFolderStore folderStore;

    @Autowired
    @Qualifier("collectionStore")
    private ICollectionStore collectionStore;

    @Autowired
    @Qualifier("resourceStore")
    private IResourceStore<AbstractResourceModel> resourceStore;

    @Autowired
    @Qualifier("resourceVersionStore")
    private IResourceVersionStore<AbstractResourceVersionModel> resourceVersionStore;

    @Autowired
    @Qualifier("textResourceStore")
    public ITextResourceStore textResourceStore;

    public ResourceContent createResourceDescription(ResourceContent resourceContent) {
        String country = resourceContent.getCountry();
        Cultures culture = Cultures.findByCountry(country);
        resourceContent.setCulture(culture);
        String value = resourceContent.getValue();
        Long resourceId = resourceContent.getResourceId();
        String lookupSuffix = resourceContent.getLookupSuffix();
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
            resourceContent.setResourceVersionId(resourceVersion.getId());

        } else if (StringUtils.isNotBlank(lookupSuffix)) {
            Long registryNodeId = resourceContent.getRegistryNodeId();
            RegistryNodeModel registryNode = registryNodeStore.findById(registryNodeId);
            CollectionModel collection = collectionStore.findOrCreateCollection(registryNode);
            boolean needToAssociate = true;
            AbstractResourceModel resource = null;
            if (lookupSuffix.equals(DESCRIPTION)) {
                ResourceType resourceType = resourceContent.getResourceType();
                String nameWithDefault = resourceContent.getResourceName() != null ?
                        resourceContent.getResourceName() : StringUtils.capitalize(DESCRIPTION);
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
                }  else if (ResourceType.IMAGE.equals(resourceType)) {
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
            resourceContent.setResourceId(resource.getId());
            resourceContent.setResourceVersionId(resourceVersion.getId());
            resourceContent.setCollectionId(collection.getId());

            if (needToAssociate) {
                collectionStore.associateCollectionWithReference(collection, resource);
            }
        }
        return resourceContent;
    }

    public ResourceContent updateResourceDescription(ResourceContent resourceContent) {
        AbstractResourceVersionModel resourceVersion =
                resourceVersionStore.findById(resourceContent.getResourceVersionId());
        if (resourceVersion instanceof TextResourceVersionModel) {
            ((TextResourceVersionModel) resourceVersion).setText(resourceContent.getValue());
        } else if (resourceVersion instanceof HtmlResourceVersionModel) {
            ((HtmlResourceVersionModel) resourceVersion).setHtml(resourceContent.getValue());
        }
        resourceVersionStore.saveOrUpdate(resourceVersion);

        return resourceContent;
    }

    public void deleteResourceDescription(Long resourceId) {
        AbstractResourceModel resource = resourceStore.findById(resourceId);
        resourceStore.delete(resource);
    }

    public Map<FieldModel, ResourceContent> createFieldsDescriptionContent(Cultures culture, List<FieldModel> fields) {
        Map<FieldModel, ResourceContent> result;
        if (fields == null) {
            result = null;
        } else {
            culture = culture == null ? Cultures.AMERICAN : culture;
            result = new HashMap<FieldModel, ResourceContent>();
            for (FieldModel fieldModel : fields) {
                if (StringUtils.isNotBlank(fieldModel.getDescription())) {
                    ResourceContent resourceContent = prepareResourceContentForField(culture, fieldModel);
                    resourceContent = createResourceDescription(resourceContent);
                    result.put(fieldModel, resourceContent);
                }
            }
        }
        return result;
    }

    public void updateFieldsDescriptionContent(Cultures culture, List<FieldModel> fields) {
        if (fields != null) {
            culture = culture == null ? Cultures.AMERICAN : culture;
            for (FieldModel fieldModel : fields) {
                if (StringUtils.isNotBlank(fieldModel.getDescription())) {
                    ResourceContent resourceContent = prepareResourceContentForField(culture, fieldModel);
                    AbstractResourceModel resource = resourceStore.findByLookup(getFieldDescriptionLookup(fieldModel));
                    AbstractResourceVersionModel resourceVersion = resource == null ? null :
                            resourceVersionStore.findLastVersionByResourceIdCulture(resource.getId(), Cultures.AMERICAN);
                    //create or update appropriate resource version
                    resourceContent.setResourceVersionId(resourceVersion == null ? null : resourceVersion.getId());
                    updateResourceDescription(resourceContent);
                }
            }
        }
    }

    public void deleteFieldDescriptionContent(FieldModel fieldModel) {
        if (fieldModel != null) {
            AbstractResourceModel resource = resourceStore.findByLookup(getFieldDescriptionLookup(fieldModel));
            if (resource != null) {
                deleteResourceDescription(resource.getId());
            }
        }
    }

    public void processActionParametersDescription(
            CollectionModel collection, Map<ActionParameterModel, Boolean> parametersDescriptionsToProcess) {
        if (parametersDescriptionsToProcess != null) {
            List<ActionParameterModel> descriptionsToAdd = new ArrayList<ActionParameterModel>();
            List<ActionParameterModel> descriptionsToUpdate = new ArrayList<ActionParameterModel>();
            List<ActionParameterModel> descriptionsToDelete = new ArrayList<ActionParameterModel>();
            for (Map.Entry<ActionParameterModel, Boolean> entry : parametersDescriptionsToProcess.entrySet()) {
                if (entry.getValue() == Boolean.TRUE) {
                    descriptionsToAdd.add(entry.getKey());
                } else if (entry.getValue() == Boolean.FALSE) {
                    descriptionsToDelete.add(entry.getKey());
                } else {
                    descriptionsToUpdate.add(entry.getKey());
                }
            }
            if (!descriptionsToAdd.isEmpty()) {
                ActionModel actionModel = descriptionsToAdd.get(0).getParent();
                textResourceStore.saveActionParameterDescriptions(actionModel, descriptionsToAdd, collection);
            }
            if (!descriptionsToUpdate.isEmpty()) {
                ActionModel actionModel = descriptionsToUpdate.get(0).getParent();
                textResourceStore.saveActionParameterDescriptions(actionModel, descriptionsToUpdate, collection);
            }
            if (!descriptionsToDelete.isEmpty()) {
                textResourceStore.deleteActionParameterDescriptions(descriptionsToDelete);
            }
        }
    }

    private String getFieldDescriptionLookupSuffix(FieldModel fieldModel) {
        return "fields." + StringUtils.normalize(fieldModel.getName());
    }

    private String getFieldDescriptionLookup(FieldModel fieldModel) {
        return fieldModel.getPath() + '.' + getFieldDescriptionLookupSuffix(fieldModel);
    }

    private ResourceContent prepareResourceContentForField(Cultures culture, FieldModel fieldModel) {
        ResourceContent resourceContent = new ResourceContent();
        resourceContent.setValue(fieldModel.getDescription());
        resourceContent.setCountry(culture.getLocale().getCountry());
        resourceContent.setLookupSuffix(getFieldDescriptionLookupSuffix(fieldModel));
        resourceContent.setLookup(fieldModel.getPath());
        resourceContent.setResourceType(ResourceType.TEXT);
        resourceContent.setRegistryNodeId(fieldModel.getParent().getId());
        return resourceContent;
    }

}