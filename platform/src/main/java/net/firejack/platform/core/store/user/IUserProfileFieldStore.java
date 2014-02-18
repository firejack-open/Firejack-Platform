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

import net.firejack.platform.core.model.user.UserProfileFieldModel;
import net.firejack.platform.core.model.user.UserProfileFieldValueModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface IUserProfileFieldStore extends IRegistryNodeStore<UserProfileFieldModel> {

    UserProfileFieldModel findById(Long id, boolean loadGroup);

    List<UserProfileFieldModel> findUserProfileFieldsByRegistryNodeId(Long registryNodeId, Long userProfileFieldGroupId);

    UserProfileFieldModel findUserProfileFieldByRegistryNodeIdAndName(Long registryNodeId, String name);

	void deleteAllByRegistryNodeId(Long registryNodeId);

    void mergeForGenerator(UserProfileFieldModel model);

    void mergeForGenerator(List<UserProfileFieldModel> modelList);

    void saveForGenerator(List<UserProfileFieldModel> modelList);

    List<UserProfileFieldModel> findGroupedProfileFields(String groupLookup);

    List<UserProfileFieldValueModel> findUserProfileFieldValuesByUserAndFields(Long userId, List<Long> profileFieldIdList);

    List<UserProfileFieldValueModel> saveUserProfileFieldModelList(List<UserProfileFieldValueModel> valueModels);

    Map<Long, List<UserProfileFieldValueModel>> findUserProfileFieldValues(List<Long> userIdList, Collection<Long> userProfileFields);

}
