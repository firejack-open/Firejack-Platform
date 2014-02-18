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

package net.firejack.platform.core.model.registry.field;

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.domain.EntityModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

@Entity
@Table(name = "opf_index_entity_reference",
       uniqueConstraints = {
               @UniqueConstraint(name = "UK_INDEX_REFERENCE_NAME",
                                 columnNames = {"id_entity", "id_index", "column_name"})
       }
)
public class IndexEntityReferenceModel extends BaseEntityModel {
    private static final long serialVersionUID = 9131376981138123627L;

    private String columnName;
    private EntityModel entityModel;
    private IndexModel index;

    @Column(name = "column_name")
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entity")
	@ForeignKey(name = "fk_entity_reference_entity")
    public EntityModel getEntityModel() {
        return entityModel;
    }

    public void setEntityModel(EntityModel entityModel) {
        this.entityModel = entityModel;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_index")
	@ForeignKey(name = "fk_entity_reference_index")
    public IndexModel getIndex() {
        return index;
    }

    public void setIndex(IndexModel index) {
        this.index = index;
    }
}
