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
import net.firejack.platform.core.config.meta.IFieldElement;

import java.util.ArrayList;
import java.util.List;


public class FieldsDiffContainer {

    private List<FieldsDiff> changes = new ArrayList<FieldsDiff>();

    /**
     * @param type
     * @param parent
     * @param field
     */
    public void addFieldsDiff(DifferenceType type, IEntityElement parent, IFieldElement field) {
        changes.add(new FieldsDiff(type, parent, field));
    }

    /**
     * @param parent
     * @param oldField
     * @param newField
     */
    public void addFieldsDiff(IEntityElement parent, IFieldElement oldField, IFieldElement newField) {
        changes.add(new FieldsDiff(parent, oldField, newField));
    }

    /**
     * @return
     */
    public List<FieldsDiff> getChanges() {
        return changes;
    }

}