package net.firejack.platform.core.model.registry.domain;
/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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


import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "opf_changes")
public class PackageChangesModel extends BaseEntityModel {
    private static final long serialVersionUID = -2176864605235831836L;

    private PackageModel packageModel;
    private RegistryNodeModel entity;
    private String description;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_package")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public PackageModel getPackageModel() {
        return packageModel;
    }

    public void setPackageModel(PackageModel packageModel) {
        this.packageModel = packageModel;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entity")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public RegistryNodeModel getEntity() {
        return entity;
    }

    public void setEntity(RegistryNodeModel entity) {
        this.entity = entity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
