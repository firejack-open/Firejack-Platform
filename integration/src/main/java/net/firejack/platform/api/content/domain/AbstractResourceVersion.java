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

package net.firejack.platform.api.content.domain;

import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.model.registry.resource.Cultures;

import java.util.Date;


public abstract class AbstractResourceVersion extends BaseEntity {
    private static final long serialVersionUID = -7782630276017113860L;

    @Property(name = "resource.id")
    private Long resourceId;
    @Property(name = "resource.lookup")
    private String resourceLookup;
    @Property
    private Integer version;
    @Property
    private Cultures culture;
    @Property
    private ResourceStatus status;
    @Property
    private Date updated;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceLookup() {
        return resourceLookup;
    }

    public void setResourceLookup(String resourceLookup) {
        this.resourceLookup = resourceLookup;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Cultures getCulture() {
        return culture;
    }

    public void setCulture(Cultures culture) {
        this.culture = culture;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
