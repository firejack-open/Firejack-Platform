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

import net.firejack.platform.core.model.TreeEntityModel;
import net.firejack.platform.core.model.registry.INavigable;
import net.firejack.platform.core.model.registry.RegistryNodeModel;


public abstract class BaseNavigableRegistryNodeElement
        <Ent extends TreeEntityModel<RegistryNodeModel>>
        extends PackageDescriptorElement<Ent> implements INavigable {

    private String serverName;
    private String parentPath;
    private String urlPath;
    private Integer port;

    @Override
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    @Override
    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseNavigableRegistryNodeElement)) return false;
        if (!super.equals(o)) return false;

        BaseNavigableRegistryNodeElement that = (BaseNavigableRegistryNodeElement) o;

        return urlPath == null ? that.urlPath == null : urlPath.equals(that.urlPath);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (urlPath != null ? urlPath.hashCode() : 0);
        return result;
    }
}
