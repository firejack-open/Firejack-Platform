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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class EntitiesDiffContainer {

    private Set<EntitiesDiff> changes = new HashSet<EntitiesDiff>();
    private Map<IEntityElement, IEntityElement> equalEntitiesDiffMap = new HashMap<IEntityElement, IEntityElement>();

    /***/
    public EntitiesDiffContainer() {
    }

    /**
     * @param changes
     * @param equalEntitiesDiffMap
     */
    public EntitiesDiffContainer(Set<EntitiesDiff> changes, Map<IEntityElement, IEntityElement> equalEntitiesDiffMap) {
        this.changes = changes;
        this.equalEntitiesDiffMap = equalEntitiesDiffMap;
    }

    /**
     * @param isADDED
     * @param entityElement
     */
    public void addEntitiesDiff(boolean isADDED, IEntityElement entityElement) {
        EntitiesDiff diff = new EntitiesDiff(isADDED ? DifferenceType.ADDED : DifferenceType.REMOVED, entityElement);
        if (!changes.contains(diff)) {
            changes.add(diff);
        }
    }

    /**
     * @param oldEntityElement
     * @param newEntityElement
     */
    public void addEqualEntityCandidates(IEntityElement oldEntityElement, IEntityElement newEntityElement) {
        equalEntitiesDiffMap.put(oldEntityElement, newEntityElement);
    }

    /**
     * @return
     */
    public Set<EntitiesDiff> getChanges() {
        return changes;
    }

    /**
     * @return
     */
    public Map<IEntityElement, IEntityElement> getEqualEntitiesDiffMap() {
        return equalEntitiesDiffMap;
    }
}