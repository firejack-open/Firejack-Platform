/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.api.registry.domain;

import net.firejack.platform.api.registry.model.BIReportLocation;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class BIReportUserField extends AbstractDTO {
    private static final long serialVersionUID = 6979069079768256976L;

    @Property
    private BIReportUser userReport;
    @Property
    private BIReportField field;
    @Property
    private BIReportLocation location;
    @Property
    private boolean expanded;
    @Property
	private int order;

    public BIReportUser getUserReport() {
        return userReport;
    }

    public void setUserReport(BIReportUser userReport) {
        this.userReport = userReport;
    }

    public BIReportField getField() {
        return field;
    }

    public void setField(BIReportField field) {
        this.field = field;
    }

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}