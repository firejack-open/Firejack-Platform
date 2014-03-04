/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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