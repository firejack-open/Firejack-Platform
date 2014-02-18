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

package net.firejack.platform.api.site.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.TreeNode;
import net.firejack.platform.core.model.registry.NavigationElementType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Comparator;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class NavigationElementTree extends TreeNode<NavigationElementTree> {
    private static final long serialVersionUID = 8356825196374281186L;

    @Property
    private String name;
    @Property
    private String path;
    @Property
    private String lookup;
    @Property
    private Integer sortPosition;
    @Property
    private String urlPath;
    @Property
    private String pageUrl;
    @Property
    private NavigationElementType elementType;

    private boolean expanded;
    private boolean expandable;
    private boolean leaf;
    private String iconCls;
    private boolean allowDrag;
    private boolean allowDrop;
    private boolean hidden;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getSortPosition() {
        return sortPosition;
    }

    public void setSortPosition(Integer sortPosition) {
        this.sortPosition = sortPosition;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public NavigationElementType getElementType() {
        return elementType;
    }

    public void setElementType(NavigationElementType elementType) {
        this.elementType = elementType;
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
        leaf = this.getChildren() == null || this.getChildren().isEmpty();
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

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public static final Comparator<NavigationElementTree> SORT_POSITION = new Comparator<NavigationElementTree>() {
        @Override
        public int compare(NavigationElementTree navigationElementTree1, NavigationElementTree navigationElementTree2) {
            Integer o1 = navigationElementTree1.getSortPosition();
            Integer o2 = navigationElementTree2.getSortPosition();
            return (o1 > o2 ? 1 : (o1.equals(o2) ? 0 : -1));
        }
    };

}
