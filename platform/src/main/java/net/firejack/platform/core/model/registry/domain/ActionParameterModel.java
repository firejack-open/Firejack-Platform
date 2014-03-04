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

package net.firejack.platform.core.model.registry.domain;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.annotation.Lookup;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.ParameterTransmissionType;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;

import javax.persistence.*;


@Entity
@Lookup(parent = RegistryNodeModel.class)
@Table(name = "opf_action_parameter")
public class ActionParameterModel extends LookupModel<ActionModel> {

    private static final long serialVersionUID = -2165262420340948389L;

    //- location (enum: path or query)
    private ParameterTransmissionType location;

    //- order (number, order dictates order of path parameters, if there are more than 1)
    private Integer orderPosition;

    //- type ( same types as fields plus text, number, decimal, date, date-time, object)
    private FieldType fieldType;


    /**
     * @return
     */
    @Enumerated
    public ParameterTransmissionType getLocation() {
        return location;
    }

    /**
     * @param location
     */
    public void setLocation(ParameterTransmissionType location) {
        this.location = location;
    }

    /**
     * @return
     */
    @Column(nullable = false, columnDefinition = "INTEGER UNSIGNED DEFAULT 0")
    public Integer getOrderPosition() {
        return orderPosition;
    }

    /**
     * @param orderPosition
     */
    public void setOrderPosition(Integer orderPosition) {
        this.orderPosition = orderPosition;
    }

    /**
     * @return
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "field_type")
    public FieldType getFieldType() {
        return fieldType;
    }

    /**
     * @param type
     */
    public void setFieldType(FieldType type) {
        this.fieldType = type;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.ACTION_PARAMETER;
    }

}
