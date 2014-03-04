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

import net.firejack.platform.core.annotation.PlaceHolder;
import net.firejack.platform.core.model.Lookup;
import net.firejack.platform.core.model.TreeEntityModel;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;


@MappedSuperclass
public abstract class LookupModel<R extends LookupModel> extends TreeEntityModel<R> implements INameProvider {

    private static final long serialVersionUID = 6661899309630613093L;
    private String path;
    private String name;
    private String lookup;
    private String description;
    private Lookup hash;

	protected LookupModel() {
	}

	protected LookupModel(Long id) {
		super(id);
	}

	/**
     * @return
     */
	@XmlAttribute
    @Column(length = 1024, nullable = false)
    public String getPath() {
        return path;
    }

    /**
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

	@XmlAttribute
	@PlaceHolder(key = "name", normalize = true)
	@Column(length = 255, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    @XmlTransient
    @PlaceHolder(key = "lookup")
    @Column(length = 1024, nullable = false)
    public String getLookup() {
        return lookup;
    }

    /**
     * @param lookup
     */
    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    /**
     * @return
     */
    @Column(columnDefinition = "MEDIUMTEXT", nullable = true)
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return
     */
    @XmlTransient
    @JoinColumn(name = "id_hash", updatable = false)
    @ForeignKey(name = "FK_REGISTRY_NODE_LOOKUP")
    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    public Lookup getHash() {
        return hash;
    }

    /**
     * @param hash
     */
    public void setHash(Lookup hash) {
        this.hash = hash;
    }

    @Transient
    public abstract RegistryNodeType getType();
}
