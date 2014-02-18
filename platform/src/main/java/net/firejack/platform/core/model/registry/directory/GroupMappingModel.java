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

package net.firejack.platform.core.model.registry.directory;

import net.firejack.platform.core.model.BaseEntityModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;


@Entity
@Table(name = "opf_group_mapping")
public class GroupMappingModel extends BaseEntityModel {

    private DirectoryModel directory;
    private GroupModel group;
    private String ldapGroupDN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_directory")
    @ForeignKey(name = "FK_GROUP_MAPPING_DIRECTORY")
    public DirectoryModel getDirectory() {
        return directory;
    }

    public void setDirectory(DirectoryModel directory) {
        this.directory = directory;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_group")
    @ForeignKey(name = "FK_GROUP_MAPPING_GROUP")
    public GroupModel getGroup() {
        return group;
    }

    public void setGroup(GroupModel group) {
        this.group = group;
    }

    @Column(name="ldap_group_dn", length = 255)
    public String getLdapGroupDN() {
        return ldapGroupDN;
    }

    public void setLdapGroupDN(String ldapGroupDN) {
        this.ldapGroupDN = ldapGroupDN;
    }

}