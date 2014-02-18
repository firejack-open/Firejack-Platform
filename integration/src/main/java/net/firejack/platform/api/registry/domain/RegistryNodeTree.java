/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.api.registry.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.TreeNode;
import net.firejack.platform.core.utils.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class RegistryNodeTree extends TreeNode<RegistryNodeTree> {
	private static final long serialVersionUID = 5161968117202085402L;

    @Property
    private String name;
    @Property(name = "description")
	private String shortDescription;
    @Property
	private String path;
    @Property
	private String lookup;
    @Property(name = "type.name", readonly = true)
	private String type;
	@Property(name = "type.entityPath", readonly = true)
	private String entityType;
	private String entitySubType;
    @Property
	private Integer sortPosition;
    @Property(name = "main.id")
	private Long mainId;
    @Property(name = "id", readonly = true)
    private Long realId;

    private boolean expanded;
    private boolean expandable;
	private boolean leaf;
    private String iconCls;
    private boolean allowDrag;
    private boolean allowDrop;

    public String getNodeId() {
        return "xnode-" + getType().toLowerCase() + "-" + getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
		return StringUtils.isNotBlank(shortDescription) ? StringUtils.cutting(shortDescription, 100) : "";
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

    public String getEntitySubType() {
        return entitySubType;
    }

    public void setEntitySubType(String entitySubType) {
        this.entitySubType = entitySubType;
    }

    public Integer getSortPosition() {
		return sortPosition;
	}

	public void setSortPosition(Integer sortPosition) {
		this.sortPosition = sortPosition;
	}

    public Long getMainId() {
        return mainId;
    }

    public void setMainId(Long mainId) {
        this.mainId = mainId;
    }

    public Long getRealId() {
        return getId();
    }

    public void setRealId(Long realId) {
        this.realId = realId;
    }

    public String getText() {
        return name;
    }

    public void setText(String text) {
        this.name = text;
    }

    public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public boolean isAllowDrag() {
        return allowDrag;
    }

    public void setAllowDrag(boolean allowDrag) {
        this.allowDrag = allowDrag;
    }

    public boolean isAllowDrop() {
        return allowDrop;
    }

    public void setAllowDrop(boolean allowDrop) {
        this.allowDrop = allowDrop;
    }

}
