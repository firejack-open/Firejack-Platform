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

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.core.config.meta.element.FolderElement;
import net.firejack.platform.core.config.meta.element.resource.CollectionElement;
import net.firejack.platform.core.config.meta.element.resource.ResourceElement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "content")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ContentDescriptor {

    private List<FolderElement> folderElements;
    private List<CollectionElement> collectionElements;
    private List<ResourceElement> resourceElements;

    public ContentDescriptor() {
    }

    public ContentDescriptor(List<FolderElement> folderElements, List<CollectionElement> collectionElements, List<ResourceElement> resourceElements) {
        this.folderElements = folderElements;
        this.collectionElements = collectionElements;
        this.resourceElements = resourceElements;
    }

    @XmlElement(name = "folder")
    public List<FolderElement> getFolderElements() {
        return folderElements;
    }

    public void setFolderElements(List<FolderElement> folderElements) {
        this.folderElements = folderElements;
    }

    @XmlElement(name = "collection")
    public List<CollectionElement> getCollectionElements() {
        return collectionElements;
    }

    public void setCollectionElements(List<CollectionElement> collectionElements) {
        this.collectionElements = collectionElements;
    }

    @XmlElement(name = "resource")
    public List<ResourceElement> getResourceElements() {
        return resourceElements;
    }

    public void setResourceElements(List<ResourceElement> resourceElements) {
        this.resourceElements = resourceElements;
    }

}
