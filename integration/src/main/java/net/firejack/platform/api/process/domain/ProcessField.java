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


import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.registry.field.Field;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.validation.annotation.NotBlank;
import net.firejack.platform.core.validation.annotation.NotNull;
import net.firejack.platform.core.validation.constraint.RuleSource;
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
@RuleSource("OPF.process.ProcessField")
public class ProcessField extends BaseEntity {
    private static final long serialVersionUID = 1239798256177992840L;

    @Property
    private String name;
    @Property
    private Field field;
    @Property
    private Boolean global;
    @Property
    private String valueType;
    @Property
    private Integer orderPosition;
    @Property
    private Entity registryNodeType;
    @Property
    private String format;

    @NotBlank
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Boolean getGlobal() {
        return global;
    }

    public void setGlobal(Boolean global) {
        this.global = global;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    @NotNull
    public Integer getOrderPosition() {
        return orderPosition;
    }

    public void setOrderPosition(Integer orderPosition) {
        this.orderPosition = orderPosition;
    }

    public Entity getRegistryNodeType() {
        return registryNodeType;
    }

    public void setRegistryNodeType(Entity registryNodeType) {
        this.registryNodeType = registryNodeType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
