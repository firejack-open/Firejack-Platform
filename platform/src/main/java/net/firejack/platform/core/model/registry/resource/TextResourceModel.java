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
import net.firejack.platform.core.model.registry.RegistryNodeType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;


@Entity
@DiscriminatorValue("TXT")
public class TextResourceModel extends ResourceModel<TextResourceVersionModel> {

    private static final long serialVersionUID = 8720779497207842639L;

    @Transient
    public TextResourceVersionModel getResourceVersion() {
        return resourceVersion;
    }

    /**
     * @param resourceVersion
     */
    public void setResourceVersion(TextResourceVersionModel resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    @Transient
    public ResourceType getResourceType() {
        return ResourceType.TEXT;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.TEXT_RESOURCE;
    }

}
