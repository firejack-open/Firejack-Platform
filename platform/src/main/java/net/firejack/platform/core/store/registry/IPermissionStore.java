/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.store.registry;

import net.firejack.platform.core.model.SpecifiedIdsFilter;
import net.firejack.platform.core.model.registry.authority.PermissionModel;
import net.firejack.platform.core.model.registry.authority.ResourceLocationModel;
import net.firejack.platform.core.model.registry.domain.ActionModel;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;
import net.firejack.platform.core.utils.Paging;

import java.util.List;
import java.util.Map;


public interface IPermissionStore extends IRegistryNodeStore<PermissionModel> {

    /**
     * @param id
     * @return
     */
    PermissionModel findByIdWithRolesAndNavElements(Long id);

    /**
     * @param term
     * @param filter
     * @return
     */
    List<PermissionModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter);

    List<PermissionModel> findAllBySearchTermWithFilter(String term, SpecifiedIdsFilter<Long> filter, Paging paging);

    /**
     * @param registryNodeIds
     * @param term
     * @param filter
     * @return
     */
    List<PermissionModel> findAllBySearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter);

    List<PermissionModel> findAllBySearchTermWithFilter(
            List<Long> registryNodeIds, String term, SpecifiedIdsFilter<Long> filter, Paging paging);

    /**
     * @param roleId
     * @return
     */
    List<PermissionModel> findRolePermissions(Long roleId);

    /**
     * @param roleId
     * @param baseLookup
     * @return
     */
    List<PermissionModel> findRolePermissions(Long roleId, String baseLookup);

    /**
     * @param roles
     * @return
     */
    Map<Long, List<PermissionModel>> findRolePermissions(List<Long> roles);

    /**
     * @param action
     */
    void createPermissionByAction(ActionModel action);

    /**
     * @param navigationElement
     */
    void createPermissionByNavigationElement(NavigationElementModel navigationElement);

    /**
     * @param resourceLocation
     */
    void createPermissionForResourceLocation(ResourceLocationModel resourceLocation);

    /**
     * @param resourceLocationId
     * @return
     */
    List<PermissionModel> findResourceLocationPermissions(Long resourceLocationId);

    String findRegistryNodeRefPath(Long permissionId);

    void save(PermissionModel registryNode);

    /**
     * @param registryNodeId
     * @param parentRegistryNodes
     */
    void updateParent(Long registryNodeId);

    /**
     * @param registryNodeId
     */
    void deleteAllByRegistryNodeId(Long registryNodeId);

}
