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
