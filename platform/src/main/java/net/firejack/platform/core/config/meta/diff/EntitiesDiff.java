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