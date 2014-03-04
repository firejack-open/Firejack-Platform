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

package net.firejack.platform.core.config.meta.element.resource;

import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.model.registry.resource.CollectionModel;
import net.firejack.platform.core.utils.CollectionUtils;

import java.util.List;


public class CollectionElement extends PackageDescriptorElement<CollectionModel> {

	private List<CollectionMemberElement> collectionMembers;

	public List<CollectionMemberElement> getCollectionMembers() {
		return collectionMembers;
	}

	public void setCollectionMembers(List<CollectionMemberElement> collectionMembers) {
		this.collectionMembers = collectionMembers;
	}

	@Override
	public Class<CollectionModel> getEntityClass() {
		return CollectionModel.class;
	}

    @Override
    public boolean equals(Object o) {
        boolean equals = super.equals(o);
        if (equals) {
            equals = o instanceof CollectionElement;
            if (equals) {
                List<CollectionMemberElement> thatCollectionMembers = ((CollectionElement) o).getCollectionMembers();
                if (!CollectionUtils.isEmpty(this.collectionMembers) &&
                        !CollectionUtils.isEmpty(thatCollectionMembers)) {
                    equals = this.collectionMembers.containsAll(thatCollectionMembers) &&
                            thatCollectionMembers.containsAll(this.collectionMembers);
                } else if (CollectionUtils.isEmpty(this.collectionMembers) ^
                        CollectionUtils.isEmpty(thatCollectionMembers)) {
                    equals = false;
                }
            }
        }
        return equals;
    }
}