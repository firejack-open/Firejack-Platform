package net.firejack.platform.core.model.registry.report;
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
import net.firejack.platform.core.model.registry.domain.RelationshipModel;
import net.firejack.platform.core.model.registry.field.FieldModel;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "opf_report_field")
public class ReportFieldModel extends BaseEntityModel {
	private static final long serialVersionUID = 7486071596061580255L;

	private ReportModel report;
	private List<RelationshipModel> relationships;
	private FieldModel field;
	private String displayName;
	private Boolean visible;
	private Boolean searchable;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_report")
	public ReportModel getReport() {
		return report;
	}

	public void setReport(ReportModel report) {
		this.report = report;
	}

    @ManyToMany
    @JoinTable(
            name = "opf_report_field_relationship",
            joinColumns = @JoinColumn(name = "id_report_field", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_relationship", referencedColumnName = "id")
    )
    @OrderColumn(name = "sort")
    public List<RelationshipModel> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<RelationshipModel> relationships) {
        this.relationships = relationships;
    }

    @ManyToOne(optional = false)
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

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getSearchable() {
        return searchable;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }
}
