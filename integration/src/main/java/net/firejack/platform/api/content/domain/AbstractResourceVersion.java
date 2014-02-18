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

package net.firejack.platform.api.content.domain;

import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.model.registry.resource.Cultures;

import java.util.Date;


public abstract class AbstractResourceVersion extends BaseEntity {
    private static final long serialVersionUID = -7782630276017113860L;

    @Property(name = "resource.id")
    private Long resourceId;
    @Property(name = "resource.lookup")
    private String resourceLookup;
    @Property
    private Integer version;
    @Property
    private Cultures culture;
    @Property
    private ResourceStatus status;
    @Property
    private Date updated;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceLookup() {
        return resourceLookup;
    }

    public void setResourceLookup(String resourceLookup) {
        this.resourceLookup = resourceLookup;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Cultures getCulture() {
        return culture;
    }

    public void setCulture(Cultures culture) {
        this.culture = culture;
    }

    public ResourceStatus getStatus() {
        return status;
    }

    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
