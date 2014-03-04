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

import net.firejack.platform.core.config.meta.INamedPackageDescriptorElement;
import net.firejack.platform.core.config.meta.diff.IPackageDescriptorElementDiff;

import java.util.List;


public class ElementDiffContainer<P extends INamedPackageDescriptorElement, T extends INamedPackageDescriptorElement> {

    private List<IPackageDescriptorElementDiff<P, T>> changes;
    private List<T> withoutChanges;

    /**
     * @param changes
     * @param withoutChanges
     */
    public ElementDiffContainer(List<IPackageDescriptorElementDiff<P, T>> changes, List<T> withoutChanges) {
        this.changes = changes;
        this.withoutChanges = withoutChanges;
    }

    /**
     * @return
     */
    public List<IPackageDescriptorElementDiff<P, T>> getChanges() {
        return changes;
    }

    /**
     * @return
     */
    public List<T> getWithoutChanges() {
        return withoutChanges;
    }
}