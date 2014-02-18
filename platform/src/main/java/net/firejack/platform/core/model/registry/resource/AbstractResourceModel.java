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

package net.firejack.platform.core.model.registry.resource;

import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.core.model.registry.RegistryNodeModel;

import javax.persistence.Entity;
import javax.persistence.Transient;


@Entity
public abstract class AbstractResourceModel<RV extends AbstractResourceVersionModel> extends RegistryNodeModel {

    private static final long serialVersionUID = 984000343228199875L;
    private Integer publishedVersion;
    private Integer lastVersion;
    private Integer selectedVersion;

    /**
     * @return
     */
    public Integer getPublishedVersion() {
        return publishedVersion;
    }

    /**
     * @param publishedVersion
     */
    public void setPublishedVersion(Integer publishedVersion) {
        this.publishedVersion = publishedVersion;
    }

    /**
     * @return
     */
    public Integer getLastVersion() {
        return lastVersion;
    }

    /**
     * @param lastVersion
     */
    public void setLastVersion(Integer lastVersion) {
        this.lastVersion = lastVersion;
    }

    /**
     * @return
     */
    @Transient
    public Integer getSelectedVersion() {
        return selectedVersion;
    }

    /**
     * @param selectedVersion
     */
    public void setSelectedVersion(Integer selectedVersion) {
        this.selectedVersion = selectedVersion;
    }

    /**
     * @return
     */
    @Transient
    public abstract RV getResourceVersion();

    /**
     * @param resourceVersion
     */
    public abstract void setResourceVersion(RV resourceVersion);

    /**
     * @return
     */
    @Transient
    public abstract ResourceType getResourceType();

}
