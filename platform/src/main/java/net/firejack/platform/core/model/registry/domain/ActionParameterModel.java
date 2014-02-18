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
