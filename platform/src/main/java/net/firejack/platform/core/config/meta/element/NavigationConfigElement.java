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

package net.firejack.platform.core.config.meta.element;

import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.NavigationElementType;
import net.firejack.platform.core.model.registry.site.NavigationElementModel;


public class NavigationConfigElement extends BaseNavigableRegistryNodeElement<NavigationElementModel> {

    private EntityProtocol protocol;
    private Integer order;
    private String pageUrl;
    private NavigationElementType type;
    private String urlParams;
    private Boolean hidden;
    private Reference reference;

    /**
     * @return
     */
    public EntityProtocol getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     */
    public void setProtocol(EntityProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @return
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * @param order
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public NavigationElementType getType() {
        return type;
    }

    public void setType(NavigationElementType type) {
        this.type = type;
    }

    public String getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(String urlParams) {
        this.urlParams = urlParams;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    @Override
    public Class<NavigationElementModel> getEntityClass() {
        return NavigationElementModel.class;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NavigationConfigElement that = (NavigationConfigElement) o;

        if (!order.equals(that.order)) return false;
        if (!pageUrl.equals(that.pageUrl)) return false;
        if ((urlParams != null && that.urlParams != null && !urlParams.equals(that.urlParams)) ||
                (urlParams == null ^ that.urlParams == null)) return false;
        //default value for hidden is 'false'. So, we consider hidden == null the same as hidden == Boolean.FALSE
        if ((hidden != null && that.hidden != null && !hidden.equals(that.hidden)) ||
                ((hidden == null || !hidden) ^ (that.hidden == null || !that.hidden))) {
            return false;
        }
        if (protocol != that.protocol) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (protocol == null ? 0 : protocol.hashCode());
        result = 31 * result + order.hashCode();
        result = 31 * result + (pageUrl == null ? 0 : pageUrl.hashCode());
        result = 31 * result + (hidden == null || !hidden ? 0 : hidden.hashCode());
        result = 31 * result + (urlParams == null ? 0 : urlParams.hashCode());
        return result;
    }
}