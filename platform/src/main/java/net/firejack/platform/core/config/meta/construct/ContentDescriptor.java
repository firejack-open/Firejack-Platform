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
