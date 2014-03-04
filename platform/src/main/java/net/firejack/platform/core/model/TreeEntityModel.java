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
