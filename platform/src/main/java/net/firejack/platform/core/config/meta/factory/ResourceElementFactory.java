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