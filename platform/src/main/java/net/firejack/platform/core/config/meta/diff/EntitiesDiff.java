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

package net.firejack.platform.core.config.meta.diff;

import net.firejack.platform.core.config.meta.IEntityElement;


public class EntitiesDiff extends PackageDescriptorElementDiff<IEntityElement, IEntityElement> {

    /**
     * This constructor instantiates diff of type DifferenceType.UPDATED
     * @param oldEntity old entity
     * @param newEntity new entity
     */
    public EntitiesDiff(IEntityElement oldEntity, IEntityElement newEntity) {
        super(oldEntity, newEntity);
    }

    /**
     * @param type difference type, in this particular case it should be DifferenceType.ADDED or DifferenceType.REMOVED
     * @param upgradeTarget target entity
     */
    public EntitiesDiff(DifferenceType type, IEntityElement upgradeTarget) {
        super(type, upgradeTarget);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(type.name());
        result.append(" : ");
        if (type == DifferenceType.UPDATED) {
            result.append(this.getDiffTarget().getPath())
                    .append('.').append(this.getDiffTarget().getName())
                    .append(" -> ").append(getNewElement().getPath())
                    .append('.').append(getNewElement().getName());

        } else {
            result.append(getDiffTarget().getPath())
                    .append('.').append(getDiffTarget().getName());
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        EntitiesDiff diff = (EntitiesDiff) obj;
        return this.type == diff.getType() &&
                this.upgradeTarget.getUid().equals(diff.getDiffTarget().getUid());
    }

}