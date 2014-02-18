/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.model.registry.domain;

import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.api.registry.model.RelationshipType;
import net.firejack.platform.core.model.registry.IAllowCreateAutoDescription;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;


@javax.persistence.Entity
@DiscriminatorValue("RSP")
public class RelationshipModel extends RegistryNodeModel implements IAllowCreateAutoDescription {
    private static final long serialVersionUID = -6788348415610760350L;

    private String hint;
    private RelationshipType relationshipType;
    private EntityModel sourceEntity;
    private EntityModel targetEntity;
    private String sourceEntityRefName;
    private String targetEntityRefName;
    private String sourceConstraintName;
    private String targetConstraintName;
    private Boolean required;
    private RelationshipOption onUpdateOption;
    private RelationshipOption onDeleteOption;
    private Boolean reverseEngineer;

	public RelationshipModel() {
	}

	public RelationshipModel(Long id) {
		super(id);
	}

	/**
     * @return display description like hint for entity
     */
    @Column(length = 255)
    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    /**
     * @return
     */
    @Enumerated
    @Column
    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    /**
     * @param relationshipType
     */
    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    /**
     * @return
     */
    @Enumerated
    public RelationshipOption getOnUpdateOption() {
        return onUpdateOption;
    }

    /**
     * @param onUpdateOption
     */
    public void setOnUpdateOption(RelationshipOption onUpdateOption) {
        this.onUpdateOption = onUpdateOption;
    }

    /**
     * @return
     */
    @Enumerated
    public RelationshipOption getOnDeleteOption() {
        return onDeleteOption;
    }

    /**
     * @param onDeleteOption
     */
    public void setOnDeleteOption(RelationshipOption onDeleteOption) {
        this.onDeleteOption = onDeleteOption;
    }

    public Boolean getReverseEngineer() {
        return reverseEngineer;
    }

    public void setReverseEngineer(Boolean reverseEngineer) {
        this.reverseEngineer = reverseEngineer;
    }

    /**
     * @return
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_source_entity")
    @ForeignKey(name = "fk_relationship_source_entity")
    public EntityModel getSourceEntity() {
        return sourceEntity;
    }

    /**
     * @param sourceEntity
     */
    public void setSourceEntity(EntityModel sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

    /**
     * @return
     */
    @Column(name = "source_entity_ref_name", length = 1024, updatable = false)
    public String getSourceEntityRefName() {
        return sourceEntityRefName;
    }

    /**
     * @param sourceEntityRefName
     */
    public void setSourceEntityRefName(String sourceEntityRefName) {
        this.sourceEntityRefName = sourceEntityRefName;
    }

    @Column(name = "source_constraint_name", length = 1024, updatable = false)
    public String getSourceConstraintName() {
        return sourceConstraintName;
    }

    public void setSourceConstraintName(String sourceDatabaseRefName) {
        this.sourceConstraintName = sourceDatabaseRefName;
    }

    /**
     * @return
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_target_entity")
    @ForeignKey(name = "fk_relationship_target_entity")
    public EntityModel getTargetEntity() {
        return targetEntity;
    }

    /**
     * @param targetEntity
     */
    public void setTargetEntity(EntityModel targetEntity) {
        this.targetEntity = targetEntity;
    }

    /**
     * @return
     */
    @Column(name = "target_entity_ref_name", length = 1024)
    public String getTargetEntityRefName() {
        return targetEntityRefName;
    }

    /**
     * @param targetEntityRefName
     */
    public void setTargetEntityRefName(String targetEntityRefName) {
        this.targetEntityRefName = targetEntityRefName;
    }

    @Column(name = "target_constraint_name", length = 1024, updatable = false)
    public String getTargetConstraintName() {
        return targetConstraintName;
    }

    public void setTargetConstraintName(String targetDatabaseRefName) {
        this.targetConstraintName = targetDatabaseRefName;
    }

    /**
     * @return
     */
    @Column(name = "required")
    public Boolean getRequired() {
        return required;
    }

    /**
     * @param required
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.RELATIONSHIP;
    }

}