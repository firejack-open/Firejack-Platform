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

package net.firejack.platform.core.model.registry;

import net.firejack.platform.core.annotation.Lookup;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

@Lookup
@javax.persistence.Entity
@Table(name = "opf_registry_node",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "id_parent"})
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@XmlTransient
public class RegistryNodeModel extends LookupModel<RegistryNodeModel> {

    public static RegistryNodeTypeComparator REGISTRY_NODE_TYPE_COMPARATOR = new RegistryNodeTypeComparator();
    private static final long serialVersionUID = 6756417700321888357L;

    @Column(name = "sort_position", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer sortPosition = 0;

    private RegistryNodeModel main;

	public RegistryNodeModel() {
	}

	public RegistryNodeModel(Long id) {
		super(id);
	}

	@Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.REGISTRY_NODE;
    }

    /**
     * @return
     */
    @XmlTransient
    public Integer getSortPosition() {
        return sortPosition;
    }

    /**
     * @param sortPosition
     */
    public void setSortPosition(Integer sortPosition) {
        this.sortPosition = sortPosition;
    }

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_main")
    @ForeignKey(name = "FK_MAIN_REGISTRY_NODE")
    public RegistryNodeModel getMain() {
        return main;
    }

    /**
     * @param main
     */
    public void setMain(RegistryNodeModel main) {
        this.main = main;
    }

}
