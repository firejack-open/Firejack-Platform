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

package net.firejack.platform.core.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;


@MappedSuperclass
public abstract class TreeEntityModel<E extends TreeEntityModel> extends UIDModel {

    private static final long serialVersionUID = -6220172014975660686L;
    protected E parent;
    protected List<E> children;
    private Integer childCount;

	protected TreeEntityModel() {
	}

	protected TreeEntityModel(Long id) {
		super(id);
	}
//    @Override
//    @Transient
//    public abstract String getDisplayName();

    /**
     * @return
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parent")
    @XmlTransient
    public E getParent() {
        return parent;
    }

    /**
     * @param parent
     */
    public void setParent(E parent) {
        this.parent = parent;
    }

	@Transient
	public List<E> getChildren() {
		return children;
	}

	public void setChildren(List<E> children) {
		this.children = children;
	}

	/**
     * @return
     */

    @Column(name = "child_count", columnDefinition = "INTEGER NOT NULL DEFAULT 0")
    @XmlTransient
    public Integer getChildCount() {
        return childCount;
    }

    /**
     * @param childCount
     */
    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    /**
     * @return
     */
    @Transient
    public abstract Enum getType();

    /**
     * @return
     */
    @Transient
    public boolean isDisplayedInTree() {
        return true;
    }
}
