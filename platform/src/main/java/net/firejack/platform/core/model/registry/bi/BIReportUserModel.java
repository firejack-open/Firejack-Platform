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
