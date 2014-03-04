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

package net.firejack.platform.api.directory.domain;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.utils.MessageResolver;
import net.firejack.platform.core.validation.annotation.DefaultValue;
import net.firejack.platform.core.validation.annotation.EnumValue;
import net.firejack.platform.core.validation.annotation.NotNull;
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
@RuleSource("OPF.directory.UserProfileField")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class UserProfileField extends Lookup {
	private static final long serialVersionUID = 986356294150314282L;

	@Property(name = "userProfileFieldGroup.id")
	private Long userProfileFieldGroupId;
    @Property(name = "userProfileFieldGroup.name")
    private String userProfileFieldGroupName;
	@Property
	private FieldType fieldType;

	public Long getUserProfileFieldGroupId() {
		return userProfileFieldGroupId;
	}

	public void setUserProfileFieldGroupId(Long userProfileFieldGroupId) {
		this.userProfileFieldGroupId = userProfileFieldGroupId;
	}

    public String getUserProfileFieldGroupName() {
        return userProfileFieldGroupName;
    }

    public void setUserProfileFieldGroupName(String userProfileFieldGroupName) {
        this.userProfileFieldGroupName = userProfileFieldGroupName;
    }

    @NotNull
	@EnumValue(enumClass = FieldType.class, hasName = true, hasDescription = true)
	@DefaultValue("INTEGER_NUMBER")
	public FieldType getFieldType() {
		return fieldType;
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldTypeName() {
		return MessageResolver.messageFormatting("net.firejack.platform.api.registry.model.FieldType." + fieldType.name() + ".name", Locale.ENGLISH);
	}
}
