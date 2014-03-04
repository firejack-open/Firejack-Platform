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

import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.validation.annotation.*;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static net.firejack.platform.core.validation.annotation.DomainType.ENTITY;
import static net.firejack.platform.core.validation.annotation.DomainType.RELATIONSHIP;

@Component
@XmlRootElement
@RuleSource("OPF.registry.Relationship")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
@Validate(type = RELATIONSHIP, parents = ENTITY, unique = {ENTITY, RELATIONSHIP})
public class Relationship extends Lookup {
	private static final long serialVersionUID = 1809915636033794233L;

    @Property
    private String hint;
	@Property
	private RelationshipType relationshipType;
	@Property
	private Entity sourceEntity;
	@Property
	private Entity targetEntity;
	@Property
	private String sourceEntityRefName;
	@Property
	private String targetEntityRefName;
	@Property
	private String sourceConstraintName;
	@Property
	private String targetConstraintName;
	@Property
	private Boolean required;
	@Property
	private RelationshipOption onUpdateOption;
	@Property
	private RelationshipOption onDeleteOption;


    @Length(maxLength = 255)
    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    @NotBlank
    @Length(maxLength = 255)
    @Match(expression = "^([A-Z][a-z0-9]*)(\\s([A-Z][a-z0-9]*))*$", msgKey = "validation.parameter.relationship.name.should.match.exp")
    public String getName() {
        return super.getName();
    }

	@NotNull
	@EnumValue(enumClass = RelationshipType.class, hasName = true, hasDescription = true)
    @DefaultValue("PARENT_CHILD")
	public RelationshipType getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(RelationshipType relationshipType) {
		this.relationshipType = relationshipType;
	}

	@NotNull
	public Entity getSourceEntity() {
		return sourceEntity;
	}

	public void setSourceEntity(Entity sourceEntity) {
		this.sourceEntity = sourceEntity;
	}

	@NotNull
	public Entity getTargetEntity() {
		return targetEntity;
	}

	public void setTargetEntity(Entity targetEntity) {
		this.targetEntity = targetEntity;
	}

	public String getSourceEntityRefName() {
		return sourceEntityRefName;
	}

	public void setSourceEntityRefName(String sourceEntityRefName) {
		this.sourceEntityRefName = sourceEntityRefName;
	}

	public String getTargetEntityRefName() {
		return targetEntityRefName;
	}

	public void setTargetEntityRefName(String targetEntityRefName) {
		this.targetEntityRefName = targetEntityRefName;
	}

    public String getSourceConstraintName() {
        return sourceConstraintName;
    }

    public void setSourceConstraintName(String sourceConstraintName) {
        this.sourceConstraintName = sourceConstraintName;
    }

    public String getTargetConstraintName() {
        return targetConstraintName;
    }

    public void setTargetConstraintName(String targetConstraintName) {
        this.targetConstraintName = targetConstraintName;
    }

    public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public RelationshipOption getOnUpdateOption() {
		return onUpdateOption;
	}

	public void setOnUpdateOption(RelationshipOption onUpdateOption) {
		this.onUpdateOption = onUpdateOption;
	}

	public RelationshipOption getOnDeleteOption() {
		return onDeleteOption;
	}

	public void setOnDeleteOption(RelationshipOption onDeleteOption) {
		this.onDeleteOption = onDeleteOption;
	}
}
