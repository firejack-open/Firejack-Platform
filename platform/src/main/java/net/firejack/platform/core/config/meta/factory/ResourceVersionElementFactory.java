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

import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.core.config.meta.element.resource.*;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.resource.*;
import org.apache.commons.beanutils.BeanUtils;


public class ResourceVersionElementFactory {

    protected ResourceVersionElementFactory() {
    }

    /**
     * @return
     */
    public static ResourceVersionElementFactory getInstance() {
        return new ResourceVersionElementFactory();
    }

    /**
     * @param resourceVersion
     * @return
     */
    public ResourceVersionElement getResourceVersionElement(AbstractResourceVersionModel resourceVersion) {
        ResourceVersionElement resourceVersionElement = null;
        if (resourceVersion instanceof TextResourceVersionModel) {
            resourceVersionElement = new TextResourceVersionElement();
        } else if (resourceVersion instanceof HtmlResourceVersionModel) {
            resourceVersionElement = new HtmlResourceVersionElement();
        } else if (resourceVersion instanceof ImageResourceVersionModel) {
            resourceVersionElement = new ImageResourceVersionElement();
        } else if (resourceVersion instanceof AudioResourceVersionModel) {
            resourceVersionElement = new AudioResourceVersionElement();
        } else if (resourceVersion instanceof VideoResourceVersionModel) {
            resourceVersionElement = new VideoResourceVersionElement();
        } else if (resourceVersion instanceof DocumentResourceVersionModel) {
            resourceVersionElement = new DocumentResourceVersionElement();
        } else if (resourceVersion instanceof FileResourceVersionModel) {
            resourceVersionElement = new FileResourceVersionElement();
        }
        try {
            BeanUtils.copyProperties(resourceVersionElement, resourceVersion);
//            if (resourceVersion instanceof IStorableResourceVersion) {
//                String storedFileName = resourceVersion.getId() + "_" + resourceVersion.getVersion() + "_" + resourceVersion.getCulture().name();
//                String resourcePath = String.valueOf(resourceVersion.getResource().getId());
//
//                ((IStorableResourceVersionDescriptorElement) resourceVersionElement).setResourceFilename(resourcePath);
//            }
        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
            throw new BusinessFunctionException(e);
        }
        return resourceVersionElement;
    }

    /**
     * @param resourceVersionElement
     * @return
     */
    public AbstractResourceVersionModel getResourceVersion(ResourceVersionElement resourceVersionElement, ResourceModel resource) {
        AbstractResourceVersionModel resourceVersion = null;
        if (resourceVersionElement instanceof TextResourceVersionElement) {
            resourceVersion = new TextResourceVersionModel();
        } else if (resourceVersionElement instanceof HtmlResourceVersionElement) {
            resourceVersion = new HtmlResourceVersionModel();
        } else if (resourceVersionElement instanceof ImageResourceVersionElement) {
            resourceVersion = new ImageResourceVersionModel();
        } else if (resourceVersionElement instanceof AudioResourceVersionElement) {
            resourceVersion = new AudioResourceVersionModel();
        } else if (resourceVersionElement instanceof VideoResourceVersionElement) {
            resourceVersion = new VideoResourceVersionModel();
        } else if (resourceVersionElement instanceof DocumentResourceVersionElement) {
            resourceVersion = new DocumentResourceVersionModel();
        } else if (resourceVersionElement instanceof FileResourceVersionElement) {
            resourceVersion = new FileResourceVersionModel();
        } else {
            throw new BusinessFunctionException("Unrecognized resource version element type.");
        }
        try {
            BeanUtils.copyProperties(resourceVersion, resourceVersionElement);
        } catch (Exception e) {
            throw new BusinessFunctionException(e);
        }
        resourceVersion.setStatus(ResourceStatus.PUBLISHED);
        resourceVersion.setResource(resource);

        return resourceVersion;
    }

    /**
     * @param resourceElement
     * @param resourceVersionElement
     */
    public void addTypedResourceVersion(ResourceElement resourceElement, ResourceVersionElement resourceVersionElement) {
        if (resourceVersionElement instanceof TextResourceVersionElement) {
            resourceElement.addTextResourceVersion((TextResourceVersionElement) resourceVersionElement);
        } else if (resourceVersionElement instanceof HtmlResourceVersionElement) {
            resourceElement.addHtmlResourceVersion((HtmlResourceVersionElement) resourceVersionElement);
        } else if (resourceVersionElement instanceof ImageResourceVersionElement) {
            resourceElement.addImageResourceVersion((ImageResourceVersionElement) resourceVersionElement);
        } else if (resourceVersionElement instanceof AudioResourceVersionElement) {
            resourceElement.addAudioResourceVersion((AudioResourceVersionElement) resourceVersionElement);
        } else if (resourceVersionElement instanceof VideoResourceVersionElement) {
            resourceElement.addVideoResourceVersion((VideoResourceVersionElement) resourceVersionElement);
        } else if (resourceVersionElement instanceof DocumentResourceVersionElement) {
            resourceElement.addDocumentResourceVersion((DocumentResourceVersionElement) resourceVersionElement);
        } else if (resourceVersionElement instanceof FileResourceVersionElement) {
            resourceElement.addFileResourceVersion((FileResourceVersionElement) resourceVersionElement);
        }
    }
}
