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

package net.firejack.platform.core.model.registry.resource;

import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.core.model.registry.RegistryNodeModel;

import javax.persistence.Entity;
import javax.persistence.Transient;


@Entity
public abstract class AbstractResourceModel<RV extends AbstractResourceVersionModel> extends RegistryNodeModel {

    private static final long serialVersionUID = 984000343228199875L;
    private Integer publishedVersion;
    private Integer lastVersion;
    private Integer selectedVersion;

    /**
     * @return
     */
    public Integer getPublishedVersion() {
        return publishedVersion;
    }

    /**
     * @param publishedVersion
     */
    public void setPublishedVersion(Integer publishedVersion) {
        this.publishedVersion = publishedVersion;
    }

    /**
     * @return
     */
    public Integer getLastVersion() {
        return lastVersion;
    }

    /**
     * @param lastVersion
     */
    public void setLastVersion(Integer lastVersion) {
        this.lastVersion = lastVersion;
    }

    /**
     * @return
     */
    @Transient
    public Integer getSelectedVersion() {
        return selectedVersion;
    }

    /**
     * @param selectedVersion
     */
    public void setSelectedVersion(Integer selectedVersion) {
        this.selectedVersion = selectedVersion;
    }

    /**
     * @return
     */
    @Transient
    public abstract RV getResourceVersion();

    /**
     * @param resourceVersion
     */
    public abstract void setResourceVersion(RV resourceVersion);

    /**
     * @return
     */
    @Transient
    public abstract ResourceType getResourceType();

}
