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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldElement;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.model.user.UserProfileFieldModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.user.IUserProfileFieldGroupStore;
import net.firejack.platform.core.store.user.IUserProfileFieldStore;
import net.firejack.platform.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 */
public class UserProfileFieldElementFactory extends
        PackageDescriptorConfigElementFactory<UserProfileFieldModel, UserProfileFieldElement> {

    @Autowired
    @Qualifier("userProfileFieldGroupStore")
    private IUserProfileFieldGroupStore userProfileFieldGroupStore;
    @Autowired
    @Qualifier("userProfileFieldStore")
    private IUserProfileFieldStore userProfileFieldStore;

    @Override
    protected void initDescriptorElementSpecific(UserProfileFieldElement descriptorElement, UserProfileFieldModel entity) {
        super.initDescriptorElementSpecific(descriptorElement, entity);
        descriptorElement.setType(entity.getFieldType());
        if (entity.getUserProfileFieldGroup() != null) {
            descriptorElement.setGroupLookup(entity.getUserProfileFieldGroup().getLookup());
        }
    }

    @Override
    protected void initEntitySpecific(UserProfileFieldModel entity, UserProfileFieldElement descriptorElement) {
        super.initEntitySpecific(entity, descriptorElement);
        entity.setFieldType(descriptorElement.getType());
        if (StringUtils.isNotBlank(descriptorElement.getGroupLookup())) {
            UserProfileFieldGroupModel profileFieldGroupModel =
                    userProfileFieldGroupStore.findByLookup(descriptorElement.getGroupLookup());
            entity.setUserProfileFieldGroup(profileFieldGroupModel);
        }
    }

    @Override
    public Class<UserProfileFieldModel> getEntityClass() {
        return UserProfileFieldModel.class;
    }

    @Override
    public Class<UserProfileFieldElement> getElementClass() {
        return UserProfileFieldElement.class;
    }

    @Override
    protected IRegistryNodeStore<?> getRefPathProviderStore() {
        return userProfileFieldStore;
    }
}