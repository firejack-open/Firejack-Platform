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

package net.firejack.platform.core.config.meta.factory;

import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldElement;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.model.user.UserProfileFieldModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.user.IUserProfileFieldGroupStore;
import net.firejack.platform.core.store.user.IUserProfileFieldStore;
import net.firejack.platform.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 */
public class UserProfileFieldElementFactory extends
        PackageDescriptorConfigElementFactory<UserProfileFieldModel, UserProfileFieldElement> {

    @Autowired
    @Qualifier("userProfileFieldGroupStore")
    private IUserProfileFieldGroupStore userProfileFieldGroupStore;
    @Autowired
    @Qualifier("userProfileFieldStore")
    private IUserProfileFieldStore userProfileFieldStore;

    @Override
    protected void initDescriptorElementSpecific(UserProfileFieldElement descriptorElement, UserProfileFieldModel entity) {
        super.initDescriptorElementSpecific(descriptorElement, entity);
        descriptorElement.setType(entity.getFieldType());
        if (entity.getUserProfileFieldGroup() != null) {
            descriptorElement.setGroupLookup(entity.getUserProfileFieldGroup().getLookup());
        }
    }

    @Override
    protected void initEntitySpecific(UserProfileFieldModel entity, UserProfileFieldElement descriptorElement) {
        super.initEntitySpecific(entity, descriptorElement);
        entity.setFieldType(descriptorElement.getType());
        if (StringUtils.isNotBlank(descriptorElement.getGroupLookup())) {
            UserProfileFieldGroupModel profileFieldGroupModel =
                    userProfileFieldGroupStore.findByLookup(descriptorElement.getGroupLookup());
            entity.setUserProfileFieldGroup(profileFieldGroupModel);
        }
    }

    @Override
    public Class<UserProfileFieldModel> getEntityClass() {
        return UserProfileFieldModel.class;
    }

    @Override
    public Class<UserProfileFieldElement> getElementClass() {
        return UserProfileFieldElement.class;
    }

    @Override
    protected IRegistryNodeStore<?> getRefPathProviderStore() {
        return userProfileFieldStore;
    }
}