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

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.annotation.Lookup;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Lookup(parent = RegistryNodeModel.class)
@Table(name = "opf_user_profile_field",
        uniqueConstraints = {
                // should be named uniquely for given registry node
                @UniqueConstraint(name = "UNIQUE_USER_PROFILE_FIELD", columnNames = {"id_parent", "name"})
        }
)
public class UserProfileFieldModel extends LookupModel<RegistryNodeModel> {

    private static final long serialVersionUID = -2126700628026455439L;
    private UserProfileFieldGroupModel userProfileFieldGroup;

    private FieldType fieldType;

    private List<UserProfileFieldValueModel> userProfileFieldValues = new ArrayList<UserProfileFieldValueModel>();

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.USER_PROFILE_FIELD;
    }

    /**
     * @return
     */
    @ManyToOne
    @JoinColumn(name = "id_user_profile_field_group")
    public UserProfileFieldGroupModel getUserProfileFieldGroup() {
        return userProfileFieldGroup;
    }

    /**
     * @param userProfileFieldGroup
     */
    public void setUserProfileFieldGroup(UserProfileFieldGroupModel userProfileFieldGroup) {
        this.userProfileFieldGroup = userProfileFieldGroup;
    }

    /**
     * @return
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "field_type")
    public FieldType getFieldType() {
        return fieldType;
    }

    /**
     * @param fieldType
     */
    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * @return
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfileField")
    @Cascade(value = {org.hibernate.annotations.CascadeType.DELETE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_USER_PROFILE_FIELD_VALUES")
    public List<UserProfileFieldValueModel> getUserProfileFieldValues() {
        return userProfileFieldValues;
    }

    /**
     * @param userProfileFieldValues
     */
    public void setUserProfileFieldValues(List<UserProfileFieldValueModel> userProfileFieldValues) {
        this.userProfileFieldValues = userProfileFieldValues;
    }
}
