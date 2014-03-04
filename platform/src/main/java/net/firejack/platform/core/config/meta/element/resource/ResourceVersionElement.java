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

package net.firejack.platform.core.config.meta.element.resource;

import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.core.model.registry.resource.Cultures;

import java.util.Date;


public class ResourceVersionElement {

    private Integer version;
    private Cultures culture;
    private ResourceStatus status;
    private Date updated;

    /**
     * @return
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return
     */
    public Cultures getCulture() {
        return culture;
    }

    /**
     * @param culture
     */
    public void setCulture(Cultures culture) {
        this.culture = culture;
    }

    /**
     * @return
     */
    public ResourceStatus getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    /**
     * @return
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * @param updated
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceVersionElement that = (ResourceVersionElement) o;

        if (culture != that.culture) return false;
        if (status != that.status) return false;
        if (updated != null ? !updated.equals(that.updated) : that.updated != null) return false;
        if (!version.equals(that.version)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + culture.hashCode();
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        return result;
    }
}
