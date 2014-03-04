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

import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.core.model.BaseEntityModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;


@javax.persistence.Entity
@Table(name = "opf_resource_version",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"version", "culture", "id_resource"})
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractResourceVersionModel<R extends AbstractResourceModel> extends BaseEntityModel {

    private static final long serialVersionUID = 5032302221461336952L;
    private R resource;
    private Integer version;
    private Cultures culture;
    private ResourceStatus status;
    private Date updated;

    /**
     * @return
     */
    @ManyToOne(targetEntity = AbstractResourceModel.class, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_resource")
    @ForeignKey(name = "fk_resource_version_resource")
    public R getResource() {
        return resource;
    }

    /**
     * @param resource
     */
    public void setResource(R resource) {
        this.resource = resource;
    }

    /**
     * @return
     */
    @Column(nullable = false)
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
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
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
    @Column(nullable = false)
    @Enumerated
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

    @Transient
    public String getResourceStoredFileName() {
        return (new StringBuilder(String.valueOf(this.getId())))
                .append('_').append(this.getVersion()).append('_')
                .append(this.getCulture().name()).toString();
    }

}
