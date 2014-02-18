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
