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

import net.firejack.platform.core.config.meta.element.profile.UserProfileFieldGroupElement;
import net.firejack.platform.core.model.user.UserProfileFieldGroupModel;
import net.firejack.platform.core.store.registry.IRegistryNodeStore;
import net.firejack.platform.core.store.user.IUserProfileFieldGroupStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 */
public class UserProfileFieldGroupElementFactory extends
        PackageDescriptorConfigElementFactory<UserProfileFieldGroupModel, UserProfileFieldGroupElement> {

    @Autowired
    @Qualifier("userProfileFieldGroupStore")
    private IUserProfileFieldGroupStore profileFieldGroupStore;

    @Override
    protected void initDescriptorElementSpecific(UserProfileFieldGroupElement descriptorElement, UserProfileFieldGroupModel entity) {
        super.initDescriptorElementSpecific(descriptorElement, entity);
    }

    @Override
    protected void initEntitySpecific(UserProfileFieldGroupModel entity, UserProfileFieldGroupElement descriptorElement) {
        super.initEntitySpecific(entity, descriptorElement);
    }

    @Override
    public Class<UserProfileFieldGroupElement> getElementClass() {
        return UserProfileFieldGroupElement.class;
    }

    @Override
    public Class<UserProfileFieldGroupModel> getEntityClass() {
        return UserProfileFieldGroupModel.class;
    }

    @Override
    protected IRegistryNodeStore<?> getRefPathProviderStore() {
        return profileFieldGroupStore;
    }
}