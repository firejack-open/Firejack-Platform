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
