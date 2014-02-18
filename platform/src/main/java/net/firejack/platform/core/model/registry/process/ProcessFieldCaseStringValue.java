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
 * Class represents process field case value entity of String type
 */
@Entity
@DiscriminatorValue(ProcessFieldType.STRING_CONST)
public class ProcessFieldCaseStringValue extends ProcessFieldCaseValue {

    private String stringValue;

    /**
     * Gets String value
     * @return process field value for a case
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * Sets String value
     * @param stringValue process field value for a case
     */
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * Gets the value
     * @return String value of the process field
     */
    @Override
    @Transient
    public Object getValue() {
        return getStringValue();
    }

    /**
     * Sets the value
     * @param value String value of the process field
     */
    @Override
    public void setValue(Object value) {
        this.stringValue = (String) value;
    }

}
