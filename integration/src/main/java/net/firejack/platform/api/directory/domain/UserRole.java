/**
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
