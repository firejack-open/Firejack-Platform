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