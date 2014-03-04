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

import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldGroupElement;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.user.IUserProfileFieldGroupStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 */
public class UserProfileFieldGroupElementFactory extends
        PackageDescriptorConfigElementFactory<UserProfileFieldGroupModel, UserProfileFieldGroupElement> {

    @Autowired
    @Qualifier("userProfileFieldGroupStore")
    private IUserProfileFieldGroupStore profileFieldGroupStore;

    @Override
    protected void initDescriptorElementSpecific(UserProfileFieldGroupElement descriptorElement, UserProfileFieldGroupModel entity) {
        super.initDescriptorElementSpecific(descriptorElement, entity);
    }

    @Override
    protected void initEntitySpecific(UserProfileFieldGroupModel entity, UserProfileFieldGroupElement descriptorElement) {
        super.initEntitySpecific(entity, descriptorElement);
    }

    @Override
    public Class<UserProfileFieldGroupElement> getElementClass() {
        return UserProfileFieldGroupElement.class;
    }

    @Override
    public Class<UserProfileFieldGroupModel> getEntityClass() {
        return UserProfileFieldGroupModel.class;
    }

    @Override
    protected IRegistryNodeStore<?> getRefPathProviderStore() {
        return profileFieldGroupStore;
    }
}