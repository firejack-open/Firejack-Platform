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

package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.registry.ProcessFieldType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Class represents process field case value entity of Integer type
 */
@Entity
@DiscriminatorValue(ProcessFieldType.INTEGER_CONST)
public class ProcessFieldCaseIntegerValue extends ProcessFieldCaseValue {

    private Integer integerValue;

    /**
     * Gets Integer value
     * @return process field value for a case
     */
    public Integer getIntegerValue() {
        return integerValue;
    }

    /**
     * Sets Integer value
     * @param integerValue process field value for a case
     */
    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    /**
     * Gets the value
     * @return Integer value of the process field
     */
    @Override
    @Transient
    public Object getValue() {
        return getIntegerValue();
    }

    /**
     * Sets the value
     * @param value Integer value of the process field
     */
    @Override
    public void setValue(Object value) {
        this.integerValue = (Integer) value;
    }

}
