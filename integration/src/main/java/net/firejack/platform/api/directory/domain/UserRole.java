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

package net.firejack.platform.api.directory.domain;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
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
public class UserRole extends BaseEntity {
	private static final long serialVersionUID = 1308404831379009339L;

	@Property
	private Role role;
    @Property
    private BaseUser user;
    @Property(name = "type")
    private String typeLookup;
    @Property(name = "internalId")
    private Long modelId;
    @Property(name = "externalId")
    private String complexPK;
    @Property(name = "securedRecord.id")
    private Long securedRecordId;
    private Boolean reduced;

	public UserRole() {
	}

	public UserRole(Role role) {
		this.role = role;
	}

    public UserRole(Role role, BaseUser user) {
        this.role = role;
        this.user = user;
    }

    public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

    public BaseUser getUser() {
        return user;
    }

    public void setUser(BaseUser user) {
        this.user = user;
    }

    public String getTypeLookup() {
        return typeLookup;
    }

    public void setTypeLookup(String typeLookup) {
        this.typeLookup = typeLookup;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getComplexPK() {
        return complexPK;
    }

    public void setComplexPK(String complexPK) {
        this.complexPK = complexPK;
    }

    public Long getSecuredRecordId() {
        return securedRecordId;
    }

    public void setSecuredRecordId(Long securedRecordId) {
        this.securedRecordId = securedRecordId;
    }

    public Boolean getReduced() {
        return reduced;
    }

    public void setReduced(Boolean reduced) {
        this.reduced = reduced;
    }
}
