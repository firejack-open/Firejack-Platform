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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.registry.authority.PermissionAssignment;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import net.firejack.platform.core.store.IStore;

import java.util.Map;
import java.util.Set;

/**
 *
 */
public interface IPermissionAssignmentStore extends IStore<PermissionAssignment, Long> {

    /**
     * @param sourcePackage
     * @param rolePermissionsMap
     */
    void saveAssignments(PackageModel sourcePackage, Map<RoleModel, Set<PermissionModel>> rolePermissionsMap);

    /**
     * @param packageId
     * @return
     */
    Map<RoleModel, Set<PermissionModel>> getPackageRoleAssignedPermissions(Long packageId);

}