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
import net.firejack.platform.core.validation.annotation.Length;
import net.firejack.platform.core.validation.annotation.Match;
import net.firejack.platform.core.validation.annotation.NotBlank;
import net.firejack.platform.core.validation.annotation.NotNull;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class BaseUser extends BaseEntity {
    private static final long serialVersionUID = 3514064409831730144L;

    @Property
	private String username;
	@Property
	private String email;
	@Property(name = "registryNode.id")
	private Long registryNodeId;
	@Property(name = "userRoles")
	private List<UserRole> userRoles;

	private boolean guest;

	@NotBlank
	@Length(minLength = 4, maxLength = 255)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@NotBlank
	@Length(maxLength = 255)
	@Match(expression = "^[\\w\\._%+-]+@[\\w\\.-]+\\.\\w{2,6}$", msgKey = "validation.parameter.should.be.email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/*@XmlTransient
	@JsonIgnore*/
    @XmlElementWrapper(name = "userRoles")
	public List<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

    @XmlTransient
	@JsonIgnore
    /*@XmlElementWrapper(name = "roles")*/
	public List<Role> getRoles() {
		List<Role> roles = new ArrayList<Role>();
		if (userRoles != null) {
			for (UserRole role : userRoles) {
				roles.add(role.getRole());
			}
		}
		return roles;
	}

	public void setRoles(List<Role> roles) {
		if (roles != null) {
			if (userRoles == null) {
				userRoles = new ArrayList<UserRole>();
			}
            userRoles.clear();
			for (Role role : roles) {
				userRoles.add(new UserRole(role, this));
			}
		}
	}

	@NotNull
	public Long getRegistryNodeId() {
		return registryNodeId;
	}

	public void setRegistryNodeId(Long registryNodeId) {
		this.registryNodeId = registryNodeId;
	}

	public boolean isGuest() {
		return guest;
	}

	public void setGuest(boolean guest) {
		this.guest = guest;
	}

}