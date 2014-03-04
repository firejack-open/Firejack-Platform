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
import net.firejack.platform.core.utils.MessageResolver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Locale;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class UserProfileFieldTree extends UserProfileFieldGroupTree {
    private static final long serialVersionUID = 7565628470329846529L;

    @Property(name = "userProfileFieldGroup.id")
	private Long userProfileFieldGroupId;
	@Property
	private FieldType fieldType;

    public Long getUserProfileFieldGroupId() {
		return userProfileFieldGroupId;
	}

    public void setUserProfileFieldGroupId(Long userProfileFieldGroupId) {
        this.userProfileFieldGroupId = userProfileFieldGroupId;
    }

	public FieldType getFieldType() {
		return fieldType;
	}

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldTypeName() {
		return MessageResolver.messageFormatting("net.firejack.platform.api.registry.model.FieldType." + fieldType.name() + ".name", Locale.ENGLISH);
	}

    @Override
    public boolean isExpanded() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isAllowDrag() {
        return true;
    }

    @Override
    public boolean isAllowDrop() {
        return false;
    }

}
