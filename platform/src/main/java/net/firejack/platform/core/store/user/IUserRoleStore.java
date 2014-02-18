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
