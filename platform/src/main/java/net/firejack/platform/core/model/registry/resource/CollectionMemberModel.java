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

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

@Entity
@Table(name = "opf_collection_membership",
       uniqueConstraints = {
               @UniqueConstraint(name = "UK_COLLECTION_REFERENCE",
                                 columnNames = {"id_collection", "id_reference"})
       }
)
public class CollectionMemberModel extends BaseEntityModel {
	private static final long serialVersionUID = -7737252460302846453L;

	private CollectionModel collection;
	private RegistryNodeModel reference;
	private int order;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_collection")
	@ForeignKey(name = "fk_registry_node_collection")
	public CollectionModel getCollection() {
		return collection;
	}

	public void setCollection(CollectionModel collection) {
		this.collection = collection;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_reference")
    @ForeignKey(name = "fk_registry_node_reference")
	public RegistryNodeModel getReference() {
		return reference;
	}

	public void setReference(RegistryNodeModel reference) {
		this.reference = reference;
	}

    @Column(name = "sort_position", nullable = false, columnDefinition = "INT DEFAULT 0")
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Transient
	public boolean compare(CollectionMemberModel member) {
		return member!=null && member.getReference().equals(getReference()) && member.getCollection().equals(getCollection());
	}
}
