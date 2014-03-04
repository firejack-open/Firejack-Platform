/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.core.model.registry.report;


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
