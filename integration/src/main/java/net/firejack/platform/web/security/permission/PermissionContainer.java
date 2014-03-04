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

package net.firejack.platform.web.security.permission;

import net.firejack.platform.api.authority.domain.UserPermission;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class PermissionContainer implements IPermissionContainer {

    private List<UserPermission> grantedActions;
    private List<UserPermission> deniedActions;

    @Override
    public List<UserPermission> loadGrantedActions(Principal p) {
        if (grantedActions == null) {
            grantedActions = Collections.emptyList();
        }
        return grantedActions;
    }

    /**
     * @param grantedActions
     */
    public void setGrantedActions(List<UserPermission> grantedActions) {
        this.grantedActions = grantedActions;
    }

    @Override
    public List<UserPermission> loadDeniedActions(Principal p) {
        if (deniedActions == null) {
            deniedActions = Collections.emptyList();
        }
        return deniedActions;
    }

    @Override
    public List<UserPermission> loadUserPermissionsBySecuredRecords(Principal p, Long securedRecordId) {
        return Collections.emptyList();
    }

    @Override
    public List<UserPermission> loadPackageLevelUserPermissions(Principal p, String packageLookup) {
        return Collections.emptyList();
    }

    @Override
    public Map<Long, List<UserPermission>> loadSecuredRecordContextPermissions(Long userId) {
        return Collections.emptyMap();
    }

    /**
     * @param deniedActions
     */
    public void setDeniedActions(List<UserPermission> deniedActions) {
        this.deniedActions = deniedActions;
    }
}
