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

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.authority.RoleModel;
import net.firejack.platform.core.model.user.BaseUserModel;
import net.firejack.platform.core.store.IStore;
import org.hibernate.criterion.Order;

import java.util.List;
import java.util.Map;


public interface IBaseUserStore<M extends BaseUserModel> extends IStore<M, Long> {

    /**
     * @param username
     * @return
     */
    M findUserByUsername(String username);

    /**
     * @param email
     * @return
     */
    M findUserByEmail(String email);

	List<M> findByRole(Long id);

    /**
     * Method finds user with all roles
     *
     * @param id ID of user
     * @return user with roles
     */
    M findByIdWithRoles(Long id);

    /**
     * @param id
     * @return
     */
    M findByIdWithRegistryNodeAndGlobalRoles(Long id);

    /**
     * @param username
     * @param password
     * @return
     */
    M findUserByUsernameAndPassword(String username, String password);

    /**
     * @param registryNodeIds
     * @param filter
     * @return
     */
    List<M> findAllByRegistryNodeIdsWithFilter(List<Long> registryNodeIds, SpecifiedIdsFilter<Long> filter);

    /**
     * @param registryNodeIds
     * @param term
     * @param filter
     * @return
     */
    List<M> findAllByRegistryNodeIdsAndSearchTermWithFilter(List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter);

    List<M> findAllByRegistryNodeIdsAndSearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter,
            Integer offset, Integer limit, Order... orders);

    /**
     * @param id
     * @return
     */
    Integer countUsersByRole(Long id);

    /**
     * @return
     */
    Map<Long, List<Long>> findAllRolesByUsers();

    /**
     * @param objectId
     * @param registryNode
     * @param filter
     * @return
     */
    List<M> findAllUsersHaveContextRolesForRegistryNodeId(Long objectId, RegistryNodeModel registryNode, SpecifiedIdsFilter<Long> filter);

    /**
     * @param count
     * @return
     */
    List<M> findLastCreatedUsers(Integer count);

    /**
     * @param user
     */
    void save(M user);

    /**
     * @param userId
     * @param roles
     */
    void saveUserRoles(Long userId, List<RoleModel> roles);

    /**
     * @param id
     */
    void deleteAllByRegistryNodeId(Long id);

    /**
     * @param actorId
     * @return
     */
    List<M> findUsersBelongingToActor(long actorId);

    List<M> findUsersBelongingToActorNotInCase(long actorId);

}
