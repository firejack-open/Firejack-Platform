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
