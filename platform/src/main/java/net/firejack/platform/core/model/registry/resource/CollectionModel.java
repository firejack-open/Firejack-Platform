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

package net.firejack.platform.core.model.registry.resource;

import net.firejack.platform.core.model.registry.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("COL")
public class CollectionModel extends RegistryNodeModel implements ISortable, IAllowDrag, IAllowDrop {
	private static final long serialVersionUID = 5091260602235062365L;

	private List<CollectionMemberModel> collectionMembers;
	private List<CollectionMemberModel> referenceMembers;

	@OneToMany(mappedBy = "collection", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OrderBy("order ASC")
	public List<CollectionMemberModel> getCollectionMembers() {
		return collectionMembers;
	}

	public void setCollectionMembers(List<CollectionMemberModel> collectionMembers) {
		this.collectionMembers = collectionMembers;
	}

	@OneToMany(mappedBy = "reference", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<CollectionMemberModel> getReferenceMembers() {
		return referenceMembers;
	}

	private void setReferenceMembers(List<CollectionMemberModel> referenceMembers) {
		this.referenceMembers = referenceMembers;
	}

	@Override
	@Transient
	public RegistryNodeType getType() {
		return RegistryNodeType.COLLECTION;
	}
}
