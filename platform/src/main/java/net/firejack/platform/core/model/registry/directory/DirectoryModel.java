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

package net.firejack.platform.core.model.registry.directory;

import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.model.registry.FieldContainerRegistryNode;
import net.firejack.platform.core.model.registry.IAllowCreateAutoDescription;
import net.firejack.platform.core.model.registry.ISortable;
import net.firejack.platform.core.model.registry.RegistryNodeType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Enumerated;
import javax.persistence.Transient;


@javax.persistence.Entity
@DiscriminatorValue("DIR")
public class DirectoryModel extends FieldContainerRegistryNode implements ISortable, IAllowCreateAutoDescription {

    private static final long serialVersionUID = 271784164368960545L;
    private DirectoryType directoryType;
    private String serverName;
    private Integer port;
    private String urlPath;
    private String baseDN;
    private String rootDN;
    private String password;
    private String ldapSchemaConfig;
    private RegistryNodeStatus status;

    /**
     * @return
     */
    @Enumerated
    public DirectoryType getDirectoryType() {
        return directoryType;
    }

    /**
     * @param directoryType
     */
    public void setDirectoryType(DirectoryType directoryType) {
        this.directoryType = directoryType;
    }

    /**
     * @return
     */
    @Column(length = 1024)
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Column
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return
     */
    @Column(length = 2048)
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * @param urlPath
     */
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    /**
     * @return
     */
    @Enumerated
    public RegistryNodeStatus getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(RegistryNodeStatus status) {
        this.status = status;
    }

    @Column(name="base_dn", length = 255)
    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    @Column(name="root_dn", length = 255)
    public String getRootDN() {
        return rootDN;
    }

    public void setRootDN(String rootDN) {
        this.rootDN = rootDN;
    }

    @Column(length = 64)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name="ldap_schema_config", length = 2048)
    public String getLdapSchemaConfig() {
        return ldapSchemaConfig;
    }

    public void setLdapSchemaConfig(String ldapSchemaConfig) {
        this.ldapSchemaConfig = ldapSchemaConfig;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.DIRECTORY;
    }

}