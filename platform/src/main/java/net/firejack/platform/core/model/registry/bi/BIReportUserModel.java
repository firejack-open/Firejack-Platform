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
import net.firejack.platform.core.model.user.UserModel;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "opf_bi_report_user")
public class BIReportUserModel extends BaseEntityModel {
    private static final long serialVersionUID = -3756351354998429461L;

    private BIReportModel report;
    private UserModel user;
    private String title;
    private String filter;
    private List<BIReportUserFieldModel> fields;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_bi_report")
    public BIReportModel getReport() {
        return report;
    }

    public void setReport(BIReportModel report) {
        this.report = report;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_user")
    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    @Column(length = 64)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @OneToMany(mappedBy = "userReport", cascade = CascadeType.ALL, orphanRemoval = true)
   	@OnDelete(action = OnDeleteAction.CASCADE)
    public List<BIReportUserFieldModel> getFields() {
        return fields;
    }

    public void setFields(List<BIReportUserFieldModel> fields) {
        this.fields = fields;
    }

}
