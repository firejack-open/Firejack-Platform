package net.firejack.platform.core.store.user;

import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;

import java.util.List;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

public interface IUserProfileFieldGroupStore extends IRegistryNodeStore<UserProfileFieldGroupModel> {

    /**
     * @param userProfileFieldGroupId
     * @param initProfileFields
     * @return
     */
    UserProfileFieldGroupModel findUserProfileFieldGroupById(Long userProfileFieldGroupId, boolean initProfileFields);

    /**
     * @param registryNodeId
     * @return
     */
    List<UserProfileFieldGroupModel> findUserProfileFieldGroupsByRegistryNodeId(Long registryNodeId);

    /**
     * @param registryNodeId
     * @param name
     * @return
     */
    UserProfileFieldGroupModel findUserProfileFieldGroupByRegistryNodeIdAndName(Long registryNodeId, String name);

	void deleteAllByRegistryNodeId(Long registryNodeId);

    void mergeForGenerator(UserProfileFieldGroupModel model);

}
