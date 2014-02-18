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
