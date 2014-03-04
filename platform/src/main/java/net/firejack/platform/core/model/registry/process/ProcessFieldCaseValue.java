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

package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.BaseEntityModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

/**
 * Class represents process fields case value entity
 */
@Entity
@Table(name = "opf_process_field_case_value")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "value_type", discriminatorType = DiscriminatorType.STRING)
public class ProcessFieldCaseValue extends BaseEntityModel {

    private CaseModel processCase;
    private ProcessFieldModel processField;

    /**
     * Gets the value
     * @return value of the process field for a case
     */
    @Transient
    public Object getValue() {
        return null;
    }

    /**
     * Sets the value
     * @param value value of the process field for a case
     */
    public void setValue(Object value) {
    }

    /**
     * Gets the case
     * @return case
     */
    @ManyToOne(targetEntity = CaseModel.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_case")
    @ForeignKey(name = "FK_PROCESS_FIELD_CASE_VALUE_CASE")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public CaseModel getCase() {
        return processCase;
    }

    /**
     * Sets the case
     * @param processCase case
     */
    public void setCase(CaseModel processCase) {
        this.processCase = processCase;
    }

    /**
     * Gets the process field
     * @return process field
     */
    @ManyToOne(targetEntity = ProcessFieldModel.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_process_field")
    @ForeignKey(name = "FK_PROCESS_FIELD_CASE_VALUE_PROCESS_FIELD")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ProcessFieldModel getProcessField() {
        return processField;
    }

    /**
     * Sets the process field
     * @param processField process field
     */
    public void setProcessField(ProcessFieldModel processField) {
        this.processField = processField;
    }

}
