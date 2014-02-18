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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.core.config.meta.element.resource.ResourceElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceVersionElement;
import net.firejack.platform.core.exception.OpenFlameRuntimeException;
import net.firejack.platform.core.model.registry.resource.*;


public class ResourceElementFactory extends PackageDescriptorConfigElementFactory<ResourceModel, ResourceElement> {

    /***/
    public ResourceElementFactory() {
        setEntityClass(ResourceModel.class);
        setElementClass(ResourceElement.class);
    }

    protected ResourceModel populateEntity(ResourceElement resourceElement) {
        ResourceModel resource;
        if (ResourceType.TEXT.equals(resourceElement.getResourceType())) {
            resource = new TextResourceModel();
        } else if (ResourceType.HTML.equals(resourceElement.getResourceType())) {
            resource = new HtmlResourceModel();
        } else if (ResourceType.IMAGE.equals(resourceElement.getResourceType())) {
            resource = new ImageResourceModel();
        } else if (ResourceType.AUDIO.equals(resourceElement.getResourceType())) {
            resource = new AudioResourceModel();
        } else if (ResourceType.VIDEO.equals(resourceElement.getResourceType())) {
            resource = new VideoResourceModel();
        } else if (ResourceType.DOCUMENT.equals(resourceElement.getResourceType())) {
            resource = new DocumentResourceModel();
        } else if (ResourceType.FILE.equals(resourceElement.getResourceType())) {
            resource = new FileResourceModel();
        } else {
            throw new OpenFlameRuntimeException("Unsupported type of resource: [" + resourceElement.getResourceType() + "]");
        }
        return resource;
    }

    @Override
    protected void initEntitySpecific(ResourceModel resource, ResourceElement resourceElement) {
        super.initEntitySpecific(resource, resourceElement);
        resource.setLastVersion(resourceElement.getLastVersion());

        //resource.setPublishedVersion(1);
    }

    @Override
    protected void initDescriptorElementSpecific(ResourceElement descriptorElement, ResourceModel entity) {
        super.initDescriptorElementSpecific(descriptorElement, entity);
        descriptorElement.setLastVersion(entity.getLastVersion());
    }

    /**
     * @param resourceElement
     * @return
     */
    public ResourceVersionElement[] getRNResourceVersions(ResourceElement resourceElement) {
        ResourceVersionElement[] resourceVersionElements;
        if (ResourceType.TEXT.equals(resourceElement.getResourceType())) {
            resourceVersionElements = resourceElement.getTextResourceVersions();
        } else if (ResourceType.HTML.equals(resourceElement.getResourceType())) {
            resourceVersionElements = resourceElement.getHtmlResourceVersions();
        } else if (ResourceType.IMAGE.equals(resourceElement.getResourceType())) {
            resourceVersionElements = resourceElement.getImageResourceVersions();
        } else if (ResourceType.AUDIO.equals(resourceElement.getResourceType())) {
            resourceVersionElements = resourceElement.getAudioResourceVersions();
        } else if (ResourceType.VIDEO.equals(resourceElement.getResourceType())) {
            resourceVersionElements = resourceElement.getVideoResourceVersions();
        } else if (ResourceType.DOCUMENT.equals(resourceElement.getResourceType())) {
            resourceVersionElements = resourceElement.getDocumentResourceVersions();
        } else if (ResourceType.FILE.equals(resourceElement.getResourceType())) {
            resourceVersionElements = resourceElement.getFileResourceVersions();
        } else {
            throw new OpenFlameRuntimeException("Unsupported type of resource: [" + resourceElement.getResourceType() + "]");
        }
        return resourceVersionElements;
    }

    @Override
    protected String getRefPath(ResourceModel entity) {
        return entity.getPath();
    }

}