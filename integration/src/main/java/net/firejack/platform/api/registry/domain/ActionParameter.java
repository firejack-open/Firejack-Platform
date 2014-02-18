package net.firejack.platform.api.registry.domain;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.model.registry.ParameterTransmissionType;
import net.firejack.platform.core.utils.MessageResolver;
import net.firejack.platform.core.validation.annotation.*;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Locale;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

@Component
@XmlRootElement
@RuleSource("OPF.registry.ActionParameter")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ActionParameter extends Lookup {
	private static final long serialVersionUID = 933027365527514287L;

	@Property
	private ParameterTransmissionType location;
	@Property
	private Integer orderPosition;
	@Property
	private FieldType fieldType;

    @Override
    @NotBlank
    @Length(maxLength = 255)
    @Match(expression = "^[a-z]\\w*$", msgKey = "validation.parameter.parameter.name.should.match.exp")
    public String getName() {
        return super.getName();
    }

	@NotBlank
	@EnumValue(enumClass = ParameterTransmissionType.class)
	@DefaultValue("PATH")
	public ParameterTransmissionType getLocation() {
		return location;
	}

	public void setLocation(ParameterTransmissionType location) {
		this.location = location;
	}

	@NotNull
	public Integer getOrderPosition() {
		return orderPosition;
	}

	public void setOrderPosition(Integer orderPosition) {
		this.orderPosition = orderPosition;
	}

	@NotBlank
	@EnumValue(enumClass = FieldType.class, hasName = true, hasDescription = true)
	@DefaultValue("TINY_TEXT")
	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

    public String getTypeName() {//used in UI
        return fieldType == null ? null : MessageResolver.messageFormatting(
                "net.firejack.platform.api.registry.model.FieldType." + fieldType.name() + ".name", Locale.ENGLISH);
    }
}
