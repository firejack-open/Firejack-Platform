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

package net.firejack.platform.core.model.user;

import net.firejack.platform.core.model.UIDModel;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "opf_user_profile_field_value",
        uniqueConstraints = {
                // allow only one user field value per profile field
                @UniqueConstraint(name = "UNIQUE_USER_PROFILE_FIELD_VALUE", columnNames = {"id_user", "id_user_profile_field"})
        }
)
public class UserProfileFieldValueModel extends UIDModel {

    private static final long serialVersionUID = -6607062620994108409L;
    private UserModel user;

    private UserProfileFieldModel userProfileField;

    private Double valueNumber;

    private String valueText;

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user")
    public UserModel getUser() {
        return user;
    }

    /**
     * @param user
     */
    public void setUser(UserModel user) {
        this.user = user;
    }

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_user_profile_field")
    public UserProfileFieldModel getUserProfileField() {
        return userProfileField;
    }

    /**
     * @param userProfileField
     */
    public void setUserProfileField(UserProfileFieldModel userProfileField) {
        this.userProfileField = userProfileField;
    }

    /**
     * @return
     */
    @Transient
    public UserProfileFieldValueType getValueType() {
        switch (userProfileField.getFieldType()) {
            case FLAG:
            case CURRENCY:
            case CREATION_TIME:
            case DATE:
            case UPDATE_TIME:
            case DECIMAL_NUMBER:
            case INTEGER_NUMBER:
            case LARGE_NUMBER:
            case EVENT_TIME:
                return UserProfileFieldValueType.NUMBER;
            case DESCRIPTION:
            case MEDIUM_TEXT:
            case SHORT_TEXT:
            case TINY_TEXT:
            case URL:
            case LONG_TEXT:
            case UNLIMITED_TEXT:
            case OBJECT:
                return UserProfileFieldValueType.TEXT;
            default:
                throw new UnsupportedOperationException("Field type not supported: " + userProfileField.getFieldType());
        }
    }

    /**
     * @return
     */
    @Transient
    public Object getValue() {
        final UserProfileFieldValueType valueType = getValueType();
        switch (valueType) {
            case NUMBER:
                if (getValueNumber() == null) {
                    return null;
                }
                switch (userProfileField.getFieldType()) {
                    case FLAG:
                        return getValueNumber().intValue() == 1;
                    case CREATION_TIME:
                    case DATE:
                    case UPDATE_TIME:
                    case EVENT_TIME:
                        return new Date(getValueNumber().longValue());
                    case INTEGER_NUMBER:
                        return getValueNumber().intValue();
                    case LARGE_NUMBER:
                        return getValueNumber().longValue();
                    default:
                        return getValueNumber();
                }
            case TEXT:
                return getValueText();
            default:
                throw new IllegalArgumentException("Field value type not supported: " + valueType);
        }
    }

    /**
     * @return
     */
    @Column(nullable = true)
    public Double getValueNumber() {
        return valueNumber;
    }

    /**
     * @param valueNumber
     */
    public void setValueNumber(Double valueNumber) {
        this.valueNumber = valueNumber;
    }

    /**
     * @return
     */
    @Column(nullable = true, columnDefinition = "MEDIUMTEXT")
    public String getValueText() {
        return valueText;
    }

    /**
     * @param valueText
     */
    public void setValueText(String valueText) {
        this.valueText = valueText;
    }
}
