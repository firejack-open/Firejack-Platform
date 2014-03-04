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
@Table(name = "opf_user_profile_field_group",
        uniqueConstraints = {
                // should be named uniquely for given registry node
                @UniqueConstraint(name = "UNIQUE_USER_PROFILE_FIELD_GROUP", columnNames = {"id_parent", "name"})
        })
public class UserProfileFieldGroupModel extends LookupModel<RegistryNodeModel> {

    private static final long serialVersionUID = -5426400114385872798L;
    private List<UserProfileFieldModel> userProfileFields = new ArrayList<UserProfileFieldModel>();

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.USER_PROFILE_FIELD_GROUP;
    }

    /**
     * @return
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfileFieldGroup")
    @Cascade(value = {org.hibernate.annotations.CascadeType.DELETE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_GROUP_TO_USER_PROFILE_FIELDS")
    public List<UserProfileFieldModel> getUserProfileFields() {
        return userProfileFields;
    }

    /**
     * @param userProfileFields
     */
    public void setUserProfileFields(List<UserProfileFieldModel> userProfileFields) {
        this.userProfileFields = userProfileFields;
    }

}
