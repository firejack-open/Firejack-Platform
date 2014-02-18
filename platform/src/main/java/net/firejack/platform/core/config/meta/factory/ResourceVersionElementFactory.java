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
