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

package net.firejack.platform.core.model.registry.authority;

import net.firejack.platform.core.model.registry.INavigableRegistryNode;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("LOC")
public class ResourceLocationModel extends RegistryNodeModel implements INavigableRegistryNode {

    private static final long serialVersionUID = 6251256905661202554L;
    private String serverName;
    private Integer port;
    private String parentPath;
    private String urlPath;
    private WildcardStyle wildcardStyle;

    private List<PermissionModel> permissions;

    @Column(length = 1024)
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Column(length = 255)
    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    @Column(length = 2048)
    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    /**
     * @return
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "wildcard_style")
    public WildcardStyle getWildcardStyle() {
        return wildcardStyle;
    }

    /**
     * @param wildcardStyle
     */
    public void setWildcardStyle(WildcardStyle wildcardStyle) {
        this.wildcardStyle = wildcardStyle;
    }

    @ManyToMany(targetEntity = PermissionModel.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "opf_resource_location_permission",
            joinColumns = @JoinColumn(name = "id_resource_location", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "id_permission", referencedColumnName = "ID")
    )
    @ForeignKey(name = "FK_RESOURCE_LOCATION_PERMISSIONS")
    public List<PermissionModel> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionModel> permissions) {
        this.permissions = permissions;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.RESOURCE_LOCATION;
    }

    @Override
    @Transient
    public boolean isDisplayedInTree() {
        return false;
    }

}