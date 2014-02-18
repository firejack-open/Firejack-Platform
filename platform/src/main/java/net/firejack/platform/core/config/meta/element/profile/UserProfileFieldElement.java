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

package net.firejack.platform.core.config.meta.element.profile;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.model.user.UserProfileFieldModel;
import net.firejack.platform.core.utils.StringUtils;

/**
 *  This class represents User Profile Field information and participates in package.xml import/export
 */
public class UserProfileFieldElement extends PackageDescriptorElement<UserProfileFieldModel> {

    private FieldType type;
    private String groupLookup;

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public String getGroupLookup() {
        return groupLookup;
    }

    public void setGroupLookup(String groupLookup) {
        this.groupLookup = groupLookup;
    }

    @Override
    public Class<UserProfileFieldModel> getEntityClass() {
        return UserProfileFieldModel.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfileFieldElement)) return false;

        UserProfileFieldElement that = (UserProfileFieldElement) o;
        return StringUtils.equals(this.getUid(), that.getUid()) && this.type == that.type &&
                StringUtils.equals(this.getName(), that.getName()) &&
                StringUtils.equals(this.getPath(), that.getPath()) &&
                StringUtils.equals(this.getDescription(), that.getDescription()) &&
                StringUtils.equals(this.getGroupLookup(), that.getGroupLookup());
    }

    @Override
    public int hashCode() {
        int result = getUid() != null ? getUid().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPath() != null ? getPath().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getGroupLookup() != null ? getGroupLookup().hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

}