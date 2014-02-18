/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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