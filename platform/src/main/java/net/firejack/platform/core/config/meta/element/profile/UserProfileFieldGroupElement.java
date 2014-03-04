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

package net.firejack.platform.core.config.meta.element.profile;

import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.utils.StringUtils;

/**
 *
 */
public class UserProfileFieldGroupElement extends PackageDescriptorElement<UserProfileFieldGroupModel> {

    @Override
    public Class<UserProfileFieldGroupModel> getEntityClass() {
        return UserProfileFieldGroupModel.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfileFieldGroupElement)) return false;

        UserProfileFieldGroupElement that = (UserProfileFieldGroupElement) o;
        return StringUtils.equals(this.getUid(), that.getUid()) &&
                StringUtils.equals(this.getName(), that.getName()) &&
                StringUtils.equals(this.getPath(), that.getPath()) &&
                StringUtils.equals(this.getDescription(), that.getDescription());
    }

    @Override
    public int hashCode() {
        int result = getUid() != null ? getUid().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPath() != null ? getPath().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}