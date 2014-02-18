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

package net.firejack.platform.api.process.domain;


import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessFieldCaseValue extends BaseEntity {
    private static final long serialVersionUID = 6493716336238558823L;

    @Property(name = "processField.name")
    private String name;
    @Property
    private Object value;
    @Property(name = "processField.value")
    private String valueType;
    @Property(name = "processField.valueType")
    private String format;
    @Property(name = "processField.id")
    private Long processFieldId;

    private String fieldLookup;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProcessFieldId() {
        return processFieldId;
    }

    public void setProcessFieldId(Long processFieldId) {
        this.processFieldId = processFieldId;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFieldLookup() {
        return fieldLookup;
    }

    public void setFieldLookup(String fieldLookup) {
        this.fieldLookup = fieldLookup;
    }
    
}