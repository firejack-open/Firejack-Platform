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
import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TreeNodeSecuredRecord extends AbstractDTO {
    private static final long serialVersionUID = 7209169138821971890L;

    @Property
    private Long id;
    @Property
    private String name;
    @Property(name = "registryNode.id")
    private Long registryNodeId;
    private String registryNodeLookup;
    @Property
    private Long externalNumberId;
    @Property
    private String externalStringId;
    private Long externalParentNumberId;
    private String externalParentStringId;
    private String parentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getExternalParentNumberId() {
        return externalParentNumberId;
    }

    public void setExternalParentNumberId(Long externalParentNumberId) {
        this.externalParentNumberId = externalParentNumberId;
    }

    public String getExternalParentStringId() {
        return externalParentStringId;
    }

    public void setExternalParentStringId(String externalParentStringId) {
        this.externalParentStringId = externalParentStringId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

}