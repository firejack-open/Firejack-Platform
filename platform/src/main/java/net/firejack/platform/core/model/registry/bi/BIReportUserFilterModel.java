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


import net.firejack.platform.core.model.BaseEntityModel;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "opf_bi_report_user_filter")
public class BIReportUserFilterModel extends BaseEntityModel {
    private static final long serialVersionUID = -3756351354998429461L;

    private BIReportUserModel userReport;
    private BIReportFieldModel field;
    private List<Long> values;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_bi_report_user")
    public BIReportUserModel getUserReport() {
        return userReport;
    }

    public void setUserReport(BIReportUserModel userReport) {
        this.userReport = userReport;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_bi_report_field")
    public BIReportFieldModel getField() {
        return field;
    }

    public void setField(BIReportFieldModel field) {
        this.field = field;
    }

    @Column(name = "`values`")
    @Type(type = "net.firejack.platform.core.utils.type.IdListUserType")
    public List<Long> getValues() {
        return values;
    }

    public void setValues(List<Long> values) {
        this.values = values;
    }
}
