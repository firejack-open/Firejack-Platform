/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.config.meta.element.resource;

import net.firejack.platform.api.content.model.ResourceStatus;
import net.firejack.platform.core.model.registry.resource.Cultures;

import java.util.Date;


public class ResourceVersionElement {

    private Integer version;
    private Cultures culture;
    private ResourceStatus status;
    private Date updated;

    /**
     * @return
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return
     */
    public Cultures getCulture() {
        return culture;
    }

    /**
     * @param culture
     */
    public void setCulture(Cultures culture) {
        this.culture = culture;
    }

    /**
     * @return
     */
    public ResourceStatus getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(ResourceStatus status) {
        this.status = status;
    }

    /**
     * @return
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * @param updated
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceVersionElement that = (ResourceVersionElement) o;

        if (culture != that.culture) return false;
        if (status != that.status) return false;
        if (updated != null ? !updated.equals(that.updated) : that.updated != null) return false;
        if (!version.equals(that.version)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + culture.hashCode();
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        return result;
    }
}
