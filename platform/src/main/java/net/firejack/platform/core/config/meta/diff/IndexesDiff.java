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

package net.firejack.platform.core.config.meta.diff;

import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IIndexElement;


public class IndexesDiff extends PackageDescriptorElementDiff<IEntityElement, IIndexElement> {

    /**
     * @param type
     * @param targetParent
     * @param upgradeTarget
     */
    public IndexesDiff(DifferenceType type, IEntityElement targetParent, IIndexElement upgradeTarget) {
        super(type, targetParent, upgradeTarget);
    }

    /**
     * @param targetParent
     * @param oldIndex
     * @param newIndex
     */
    public IndexesDiff(IEntityElement targetParent, IIndexElement oldIndex, IIndexElement newIndex) {
        super(DifferenceType.UPDATED, targetParent, oldIndex, newIndex);
    }

}