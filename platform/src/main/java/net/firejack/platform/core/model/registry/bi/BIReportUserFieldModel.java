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

package net.firejack.platform.core.model.registry.bi;


import net.firejack.platform.api.registry.model.BIReportLocation;
import net.firejack.platform.core.model.BaseEntityModel;

import javax.persistence.*;

@Entity
@Table(name = "opf_bi_report_user_field")
public class BIReportUserFieldModel extends BaseEntityModel {
    private static final long serialVersionUID = 751766275826888137L;

    private BIReportUserModel userReport;
	private BIReportFieldModel field;
	private BIReportLocation location;
    private boolean expanded;
	private int order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_bi_report_user")
    public BIReportUserModel getUserReport() {
        return userReport;
    }

    public void setUserReport(BIReportUserModel userReport) {
        this.userReport = userReport;
    }

    @ManyToOne(optional = false)
	@JoinColumn(name = "id_bi_report_field")
    public BIReportFieldModel getField() {
        return field;
    }

    public void setField(BIReportFieldModel field) {
        this.field = field;
    }

    @Enumerated
    public BIReportLocation getLocation() {
        return location;
    }

    public void setLocation(BIReportLocation location) {
        this.location = location;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Column(name = "`order`")
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
