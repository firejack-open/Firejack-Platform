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
