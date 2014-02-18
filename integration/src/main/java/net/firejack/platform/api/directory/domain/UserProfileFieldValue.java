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

package net.firejack.platform.api.directory.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.validation.annotation.NotNull;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@Component
@XmlRootElement
@RuleSource("OPF.directory.UserProfileFieldValue")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class UserProfileFieldValue extends AbstractDTO {

    @Property(name = "userProfileField.id")
    private Long userProfileFieldId;
    @Property(name = "userProfileField.lookup")
    private String userProfileFieldLookup;
    @Property(name = "user.id")
    private Long userId;
    @Property
    private Double valueNumber;
    @Property
    private String valueText;

    public Long getUserProfileFieldId() {
        return userProfileFieldId;
    }

    public void setUserProfileFieldId(Long userProfileFieldId) {
        this.userProfileFieldId = userProfileFieldId;
    }

    public String getUserProfileFieldLookup() {
        return userProfileFieldLookup;
    }

    public void setUserProfileFieldLookup(String userProfileFieldLookup) {
        this.userProfileFieldLookup = userProfileFieldLookup;
    }

    @NotNull
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getValueNumber() {
        return valueNumber;
    }

    public void setValueNumber(Double valueNumber) {
        this.valueNumber = valueNumber;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }
}