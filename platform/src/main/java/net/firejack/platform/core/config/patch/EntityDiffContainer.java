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