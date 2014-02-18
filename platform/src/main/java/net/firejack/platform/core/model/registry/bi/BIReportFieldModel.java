package net.firejack.platform.core.model.registry.bi;
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
import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.model.registry.field.FieldModel;

import javax.persistence.*;

@Entity
@Table(name = "opf_bi_report_field")
public class BIReportFieldModel extends BaseEntityModel {
	private static final long serialVersionUID = 7486071596061580255L;

	private BIReportModel report;
    private EntityModel entity;
	private FieldModel field;
	private String displayName;
	private Integer count;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_bi_report")
    public BIReportModel getReport() {
        return report;
    }

    public void setReport(BIReportModel report) {
        this.report = report;
    }

    @ManyToOne(optional = false)
	@JoinColumn(name = "id_entity")
    public EntityModel getEntity() {
        return entity;
    }

    public void setEntity(EntityModel entity) {
        this.entity = entity;
    }

    @ManyToOne(optional = true)
	@JoinColumn(name = "id_field")
	public FieldModel getField() {
		return field;
	}

	public void setField(FieldModel field) {
		this.field = field;
	}

	@Column(length = 64)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

    @Column(nullable = true)
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
