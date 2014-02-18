/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
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
