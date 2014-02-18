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

package net.firejack.platform.core.model.registry.wizard;

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.model.registry.field.FieldModel;

import javax.persistence.*;

@Entity
@Table(name = "opf_wizard_field")
public class WizardFieldModel extends BaseEntityModel {
    private static final long serialVersionUID = 6096759521164420219L;

    private WizardModel wizard;
    private WizardFieldModel form;
    private FieldModel field;
    private RelationshipModel relationship;
    private String displayName;
    private Boolean editable;
    private Integer position;
    private String defaultValue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_wizard")
    public WizardModel getWizard() {
        return wizard;
    }

    public void setWizard(WizardModel wizard) {
        this.wizard = wizard;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_form")
    public WizardFieldModel getForm() {
        return form;
    }

    public void setForm(WizardFieldModel form) {
        this.form = form;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_field")
    public FieldModel getField() {
        return field;
    }

    public void setField(FieldModel field) {
        this.field = field;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_relationship")
    public RelationshipModel getRelationship() {
        return relationship;
    }

    public void setRelationship(RelationshipModel relationship) {
        this.relationship = relationship;
    }

    @Column(length = 64)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Column(name= "default_value", length = 255)
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
