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