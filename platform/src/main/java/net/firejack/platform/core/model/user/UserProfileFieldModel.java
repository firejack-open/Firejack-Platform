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

package net.firejack.platform.core.model.user;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.annotation.Lookup;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Lookup(parent = RegistryNodeModel.class)
@Table(name = "opf_user_profile_field",
        uniqueConstraints = {
                // should be named uniquely for given registry node
                @UniqueConstraint(name = "UNIQUE_USER_PROFILE_FIELD", columnNames = {"id_parent", "name"})
        }
)
public class UserProfileFieldModel extends LookupModel<RegistryNodeModel> {

    private static final long serialVersionUID = -2126700628026455439L;
    private UserProfileFieldGroupModel userProfileFieldGroup;

    private FieldType fieldType;

    private List<UserProfileFieldValueModel> userProfileFieldValues = new ArrayList<UserProfileFieldValueModel>();

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.USER_PROFILE_FIELD;
    }

    /**
     * @return
     */
    @ManyToOne
    @JoinColumn(name = "id_user_profile_field_group")
    public UserProfileFieldGroupModel getUserProfileFieldGroup() {
        return userProfileFieldGroup;
    }

    /**
     * @param userProfileFieldGroup
     */
    public void setUserProfileFieldGroup(UserProfileFieldGroupModel userProfileFieldGroup) {
        this.userProfileFieldGroup = userProfileFieldGroup;
    }

    /**
     * @return
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "field_type")
    public FieldType getFieldType() {
        return fieldType;
    }

    /**
     * @param fieldType
     */
    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * @return
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfileField")
    @Cascade(value = {org.hibernate.annotations.CascadeType.DELETE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ForeignKey(name = "FK_USER_PROFILE_FIELD_VALUES")
    public List<UserProfileFieldValueModel> getUserProfileFieldValues() {
        return userProfileFieldValues;
    }

    /**
     * @param userProfileFieldValues
     */
    public void setUserProfileFieldValues(List<UserProfileFieldValueModel> userProfileFieldValues) {
        this.userProfileFieldValues = userProfileFieldValues;
    }
}
