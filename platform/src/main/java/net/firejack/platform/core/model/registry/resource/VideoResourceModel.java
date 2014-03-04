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
import net.firejack.platform.core.model.registry.RegistryNodeType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;


@Entity
@DiscriminatorValue("VID")
public class VideoResourceModel extends ResourceModel<VideoResourceVersionModel> {

    private static final long serialVersionUID = -2328681968011827816L;

    @Transient
    public VideoResourceVersionModel getResourceVersion() {
        return resourceVersion;
    }

    /**
     * @param resourceVersion
     */
    public void setResourceVersion(VideoResourceVersionModel resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    @Transient
    public ResourceType getResourceType() {
        return ResourceType.VIDEO;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.VIDEO_RESOURCE;
    }

}
