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
