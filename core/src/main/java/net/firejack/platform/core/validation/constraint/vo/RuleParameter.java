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

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RuleParameter extends AbstractDTO {
    private static final long serialVersionUID = 7881817513758919560L;

    private String key;
    private Object value;

    /***/
    public RuleParameter() {
    }

    /**
     * @param key
     * @param value
     */
    public RuleParameter(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }
}
