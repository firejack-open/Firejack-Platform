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

package net.firejack.platform.core.config.patch;

import net.firejack.platform.core.config.meta.diff.EntitiesDiff;
import net.firejack.platform.core.config.meta.diff.FieldsDiff;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class EntityDiffContainer {

    private List<EntitiesDiff> entitiesDiffList;
    private List<FieldsDiff> fieldDiffList;

    /***/
    public EntityDiffContainer() {
    }

    /**
     * @param entitiesDiffList
     * @param fieldDiffList
     */
    public EntityDiffContainer(List<EntitiesDiff> entitiesDiffList,
                               List<FieldsDiff> fieldDiffList) {
        this.entitiesDiffList = entitiesDiffList;
        this.fieldDiffList = fieldDiffList;
    }

    /**
     * @return
     */
    public List<EntitiesDiff> getEntitiesDiffList() {
        if (entitiesDiffList == null) {
            this.entitiesDiffList = new ArrayList<EntitiesDiff>();
        }
        return entitiesDiffList;
    }

    /**
     * @return
     */
    public List<FieldsDiff> getFieldDiffList() {
        if (fieldDiffList == null) {
            this.fieldDiffList = new ArrayList<FieldsDiff>();
        }
        return fieldDiffList;
    }

    /**
     * @param diffs
     */
    public void addFieldDiffs(List<FieldsDiff> diffs) {
        getFieldDiffList().addAll(diffs);
    }

    /**
     * @param diff
     */
    public void addFieldDiff(FieldsDiff diff) {
        getFieldDiffList().add(diff);
    }

    /**
     * @param diffs
     */
    public void addEntityDiffs(Set<EntitiesDiff> diffs) {
        getEntitiesDiffList().addAll(diffs);
    }

    /**
     * @param diff
     */
    public void addEntityDiff(EntitiesDiff diff) {
        if (!getEntitiesDiffList().contains(diff)) {
            getEntitiesDiffList().add(diff);
        }
    }

}