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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;


@Entity
public class ResourceModel<RV extends AbstractResourceVersionModel> extends AbstractResourceModel<RV> {

	private static final long serialVersionUID = 5574677521298661753L;
	protected RV resourceVersion;
	private List<CollectionMemberModel> members;

    @Override
    @Transient
    public RV getResourceVersion() {
        return resourceVersion;
    }

    @Override
    public void setResourceVersion(RV resourceVersion) {
        this.resourceVersion = resourceVersion;
    }


	@OneToMany(mappedBy = "reference", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<CollectionMemberModel> getMembers() {
		return members;
	}

	private void setMembers(List<CollectionMemberModel> members) {
		this.members = members;
	}

    @Override
    @Transient
    public ResourceType getResourceType() {
        return null;
    }

    @Override
    @Transient
    public boolean isDisplayedInTree() {
        return false;
    }

}
