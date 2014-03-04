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

package net.firejack.platform.api.registry.domain;

import net.firejack.platform.api.authority.domain.Role;
import net.firejack.platform.api.registry.field.Field;
import net.firejack.platform.api.registry.field.Index;
import net.firejack.platform.api.registry.model.EntityType;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.validation.annotation.*;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import java.util.List;

import static net.firejack.platform.core.validation.annotation.DomainType.*;

@Component
@XmlRootElement
@RuleSource("OPF.registry.Entity")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
@Validate(type = ENTITY, parents = {PACKAGE, DOMAIN, ENTITY}, unique = {ENTITY, RELATIONSHIP, REPORT, ACTOR, PROCESS})
public class Entity extends Lookup {
	private static final long serialVersionUID = 66302883201266307L;

	@Property
	private String serverName;
	@Property
	private Integer port;
	@Property
	private String parentPath;
	@Property
	private String urlPath;
	@Property
	private EntityProtocol protocol;
	@Property
	private HTTPMethod method;
	@Property
	private RegistryNodeStatus status;

	@Property
	private Boolean abstractEntity;
	@Property(name = "typeEntity")
	private Boolean entType;
	@Property
	private Entity extendedEntity;
	@Property
	private List<Field> fields;
    @Property
	private List<Index> indexes;
    @Property
    private List<Role> contextRoles;
    @Property
    private Boolean securityEnabled;
    @Property
    private String databaseRefName;

    @Property
    private ReferenceObject referenceObject;
    @Property
    private Boolean reverseEngineer;

    @Override
    @NotBlank
    @Length(maxLength = 255)
    @Match(expression = "^([A-Z][a-z0-9]*)(\\s([A-Z][a-z0-9]*))*$", msgKey = "validation.parameter.entity.name.should.match.exp")
    @NotMatch(expression = "\\b(?:Package|Class)\\b", example = "'Package, Class'")
    public String getName() {
        return super.getName();
    }

	@Condition("serverNameMethodCondition")
	@Length(maxLength = 255)
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Condition("portMethodCondition")
	@Match(expression = "^\\d{2,5}$")
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Condition("parentPathMethodCondition")
	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	@Condition("pathMethodCondition")
	@Length(maxLength = 2048)
    @Match(expression = "^(/[a-z0-9\\-]+)+$", example = "/url/path/example")
	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	@EnumValue(enumClass = EntityProtocol.class, nullable = true)
	public EntityProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(EntityProtocol protocol) {
		this.protocol = protocol;
	}

	public HTTPMethod getMethod() {
		return method;
	}

	public void setMethod(HTTPMethod method) {
		this.method = method;
	}

	@NotNull
	@DefaultValue("UNKNOWN")
	public RegistryNodeStatus getStatus() {
		return status;
	}

	public void setStatus(RegistryNodeStatus status) {
		this.status = status;
	}

	@XmlTransient
	public Boolean getAbstractEntity() {
		return abstractEntity;
	}

	public void setAbstractEntity(Boolean abstractEntity) {
		this.abstractEntity = abstractEntity;
	}

	@XmlTransient
	public Boolean getEntType() {
		return entType;
	}

	public void setEntType(Boolean entType) {
		this.entType = entType;
	}

	public Entity getExtendedEntity() {
		return extendedEntity;
	}

	public void setExtendedEntity(Entity extendedEntity) {
		this.extendedEntity = extendedEntity;
	}

	public String getTypeEntity() {
		if (Boolean.TRUE.equals(getAbstractEntity())) {
			return EntityType.CLASSIFIER.getEntityType();
		} else if (Boolean.TRUE.equals(getEntType())) {
			return EntityType.DATA.getEntityType();
		} else {
			return EntityType.STANDARD.getEntityType();
		}
	}

	public void setTypeEntity(String typeEntity) {
		if (EntityType.CLASSIFIER.getEntityType().equalsIgnoreCase(typeEntity)) {
			setAbstractEntity(true);
			setEntType(false);
		} else if (EntityType.DATA.getEntityType().equalsIgnoreCase(typeEntity)) {
			setAbstractEntity(false);
			setEntType(true);
		} else if (EntityType.STANDARD.getEntityType().equalsIgnoreCase(typeEntity)) {
			setAbstractEntity(false);
			setEntType(false);
		}
	}

    @ValidateNested(iterable = true)
	@XmlElementWrapper(name = "fields")
	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

    @XmlElementWrapper(name = "indexes")
    public List<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = indexes;
    }

    public Boolean getSecurityEnabled() {
        return securityEnabled;
    }

    public void setSecurityEnabled(Boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    public List<Role> getContextRoles() {
        return contextRoles;
    }

    public void setContextRoles(List<Role> contextRoles) {
        this.contextRoles = contextRoles;
    }

    public String getDatabaseRefName() {
        return databaseRefName;
    }

    public void setDatabaseRefName(String databaseRefName) {
        this.databaseRefName = databaseRefName;
    }

    public ReferenceObject getReferenceObject() {
        return referenceObject;
    }

    public void setReferenceObject(ReferenceObject referenceObject) {
        this.referenceObject = referenceObject;
    }

    public Boolean getReverseEngineer() {
        return reverseEngineer;
    }

    public void setReverseEngineer(Boolean reverseEngineer) {
        this.reverseEngineer = reverseEngineer;
    }
}
