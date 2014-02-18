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
