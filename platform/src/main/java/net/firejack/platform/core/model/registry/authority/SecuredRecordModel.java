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

package net.firejack.platform.core.model.registry.authority;

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "opf_secured_record"
//        ,uniqueConstraints = {@UniqueConstraint(columnNames = {"registryNode", "externalNumberId"})}
)
public class SecuredRecordModel extends BaseEntityModel {

    private static final long serialVersionUID = -8777359940295985390L;
    private String name;
    private String description;
    private String paths;
    private RegistryNodeModel registryNode;
    private Long externalNumberId;
    private String externalStringId;
    private List<SecuredRecordModel> parentSecuredRecords;

    /**
     * @return
     */
    @Column(name = "name", length = 255)
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    @Column(name = "description", length = 2047)
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return
     */
    @Column(name = "paths", columnDefinition = "LONGTEXT")
    public String getPaths() {
        return paths;
    }

    /**
     * @param paths
     */
    public void setPaths(String paths) {
        this.paths = paths;
    }

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registry_node", nullable = false)
    public RegistryNodeModel getRegistryNode() {
        return registryNode;
    }

    /**
     * @param registryNode
     */
    public void setRegistryNode(RegistryNodeModel registryNode) {
        this.registryNode = registryNode;
    }

    /**
     * @return
     */
    @Column(name = "external_number_id")
    public Long getExternalNumberId() {
        return externalNumberId;
    }

    /**
     * @param externalNumberId
     */
    public void setExternalNumberId(Long externalNumberId) {
        this.externalNumberId = externalNumberId;
    }

    /**
     * @return
     */
    @Column(name = "external_string_id", length = 255)
    public String getExternalStringId() {
        return externalStringId;
    }

    /**
     * @param externalStringId
     */
    public void setExternalStringId(String externalStringId) {
        this.externalStringId = externalStringId;
    }

    /**
     * @return
     */
    @OneToMany
    @JoinTable(
            name = "opf_secured_record_parent",
            joinColumns = @JoinColumn(name = "id_child"),
            inverseJoinColumns = @JoinColumn(name = "id_parent"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_child", "id_parent"})}
    )
    public List<SecuredRecordModel> getParentSecuredRecords() {
        return parentSecuredRecords;
    }

    /**
     * @param parentSecuredRecords
     */
    public void setParentSecuredRecords(List<SecuredRecordModel> parentSecuredRecords) {
        this.parentSecuredRecords = parentSecuredRecords;
    }

    /**
     * @param parentSecuredRecord
     */
    public void addParentSecuredRecords(SecuredRecordModel parentSecuredRecord) {
        if (this.parentSecuredRecords == null) {
            this.parentSecuredRecords = new ArrayList<SecuredRecordModel>();
        }
        Set<SecuredRecordModel> parentSecuredRecordSet = new HashSet<SecuredRecordModel>(parentSecuredRecords);
        parentSecuredRecordSet.add(parentSecuredRecord);
        this.parentSecuredRecords = new ArrayList<SecuredRecordModel>(parentSecuredRecordSet);
    }

    /**
     * @param parentSecuredRecord
     */
    public void removeParentSecuredRecords(SecuredRecordModel parentSecuredRecord) {
        if (this.parentSecuredRecords != null) {
            Set<SecuredRecordModel> parentSecuredRecordSet = new HashSet<SecuredRecordModel>(parentSecuredRecords);
            parentSecuredRecordSet.remove(parentSecuredRecord);
            this.parentSecuredRecords = new ArrayList<SecuredRecordModel>(parentSecuredRecordSet);
        }
    }

}
