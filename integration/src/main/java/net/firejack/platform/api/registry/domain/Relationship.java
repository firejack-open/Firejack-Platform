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
