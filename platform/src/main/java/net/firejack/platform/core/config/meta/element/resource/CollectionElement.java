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

package net.firejack.platform.core.config.meta.element.resource;

import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.utils.CollectionUtils;

import java.util.List;


public class CollectionElement extends PackageDescriptorElement<CollectionModel> {

	private List<CollectionMemberElement> collectionMembers;

	public List<CollectionMemberElement> getCollectionMembers() {
		return collectionMembers;
	}

	public void setCollectionMembers(List<CollectionMemberElement> collectionMembers) {
		this.collectionMembers = collectionMembers;
	}

	@Override
	public Class<CollectionModel> getEntityClass() {
		return CollectionModel.class;
	}

    @Override
    public boolean equals(Object o) {
        boolean equals = super.equals(o);
        if (equals) {
            equals = o instanceof CollectionElement;
            if (equals) {
                List<CollectionMemberElement> thatCollectionMembers = ((CollectionElement) o).getCollectionMembers();
                if (!CollectionUtils.isEmpty(this.collectionMembers) &&
                        !CollectionUtils.isEmpty(thatCollectionMembers)) {
                    equals = this.collectionMembers.containsAll(thatCollectionMembers) &&
                            thatCollectionMembers.containsAll(this.collectionMembers);
                } else if (CollectionUtils.isEmpty(this.collectionMembers) ^
                        CollectionUtils.isEmpty(thatCollectionMembers)) {
                    equals = false;
                }
            }
        }
        return equals;
    }
}