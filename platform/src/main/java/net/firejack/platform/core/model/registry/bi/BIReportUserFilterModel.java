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
