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

package net.firejack.platform.core.store.user;

import net.firejack.platform.core.model.registry.authority.SecuredRecordModel;
import net.firejack.platform.core.model.registry.authority.UserRoleModel;
import net.firejack.platform.core.model.registry.authority.UserRolePk;
import net.firejack.platform.core.store.IStore;

import java.util.List;
import java.util.Map;


public interface IUserRoleStore extends IStore<UserRoleModel, UserRolePk> {

    /**
     * @param userId
     * @return
     */
    List<UserRoleModel> findAllByUserId(Long userId);

    /**
     * @param userId
     * @return
     */
    List<UserRoleModel> findAllWithPermissionsByUserId(Long userId);

    /**
     * @param userId
     * @param baseLookup
     * @return
     */
    List<UserRoleModel> findAllWithPermissionsByUserIdAndBaseLookup(Long userId, String baseLookup);

    /**
     * @param userId
     * @return
     */
    List<UserRoleModel> findGlobalRoles(Long userId);

    /**
     * @param userId
     * @param roleId
     * @param objectId
     * @param objectType
     * @return
     */
    UserRoleModel findContextRole(Long userId, Long roleId, Long objectId, String objectType);

    /**
     * @param userId
     * @param objectId
     * @param objectType
     * @return
     */
    List<UserRoleModel> findContextRolesByUserIdAndRegistryNodeId(Long userId, Long objectId, String objectType);

    /**
     * @return
     */
    Map<SecuredRecordModel, List<UserRoleModel>> findAllContextRolesBySecuredRecords();

    /**
     * @return
     */
    List<UserRoleModel> findAllContextRolesNotBoundToSecuredRecord();

    Map<String, List<UserRoleModel>> findAllPackageLevelUserRoles();

}
