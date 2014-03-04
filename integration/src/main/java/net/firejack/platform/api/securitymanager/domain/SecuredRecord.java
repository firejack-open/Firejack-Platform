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

package net.firejack.platform.api.securitymanager.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SecuredRecord extends BaseEntity {
    private static final long serialVersionUID = -8855738457294327054L;

    @Property
    private String name;
    private String description;
    @Property
    private String paths;
    @Property(name = "registryNode.id")
    private Long registryNodeId;
    private String registryNodeLookup;
    @Property
    private Long externalNumberId;
    @Property
    private String externalStringId;
    private List<SecuredRecord> securedRecords;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public Long getRegistryNodeId() {
        return registryNodeId;
    }

    public void setRegistryNodeId(Long registryNodeId) {
        this.registryNodeId = registryNodeId;
    }

    public String getRegistryNodeLookup() {
        return registryNodeLookup;
    }

    public void setRegistryNodeLookup(String registryNodeLookup) {
        this.registryNodeLookup = registryNodeLookup;
    }

    public Long getExternalNumberId() {
        return externalNumberId;
    }

    public void setExternalNumberId(Long externalNumberId) {
        this.externalNumberId = externalNumberId;
    }

    public String getExternalStringId() {
        return externalStringId;
    }

    public void setExternalStringId(String externalStringId) {
        this.externalStringId = externalStringId;
    }

    @XmlElementWrapper(name = "securedRecords")
    public List<SecuredRecord> getSecuredRecords() {
        return securedRecords;
    }

    public void setSecuredRecords(List<SecuredRecord> securedRecords) {
        this.securedRecords = securedRecords;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SecuredRecord");
        sb.append("{name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", paths='").append(paths).append('\'');
        sb.append(", registryNodeId=").append(registryNodeId);
        sb.append(", registryNodeLookup='").append(registryNodeLookup).append('\'');
        sb.append(", externalNumberId=").append(externalNumberId);
        sb.append(", externalStringId='").append(externalStringId).append('\'');
        sb.append(", securedRecords=").append(securedRecords);
        sb.append('}');
        return sb.toString();
    }
}