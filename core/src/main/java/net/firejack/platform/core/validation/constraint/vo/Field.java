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

package net.firejack.platform.core.validation.constraint.vo;

import net.firejack.platform.core.domain.AbstractDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Field extends AbstractDTO {
    private static final long serialVersionUID = -9068246538644275557L;

    private String name;
    private Object defaultValue;
    private List<Constraint> constraints;
    private Boolean editable;

    /***/
    public Field() {
    }

    /**
     * @param name
     */
    public Field(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return
     */
    public List<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * @param constraints
     */
    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    /**
     * @return
     */
    public Boolean getEditable() {
        return editable;
    }

    /**
     * @param editable
     */
    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

}
