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

package net.firejack.platform.core.model.registry.system;

import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.annotation.PlaceHolder;
import net.firejack.platform.core.annotation.PlaceHolders;
import net.firejack.platform.core.model.registry.IAllowCreateAutoDescription;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Class represents registry node of the server type
 */
@Entity
@DiscriminatorValue("FLS")
@PlaceHolders(name = "filestore", holders = {
        @PlaceHolder(key = "{name}.directory", value = "{serverDirectory}")
})
public class FileStoreModel extends RegistryNodeModel implements IAllowCreateAutoDescription {

    private static final long serialVersionUID = -4443702156989006414L;
    private String serverName;
    private Integer port;
    private String urlPath;
    private RegistryNodeStatus status;
    private String serverDirectory;

    /**
     * Gets the server name
     *
     * @return - name of the server
     */
    @Column(length = 1024)
    public String getServerName() {
        return serverName;
    }

    /**
     * Sets the server name
     *
     * @param serverName - name of the server
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Gets the port
     *
     * @return - server port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the port
     *
     * @param port - server port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Gets the URL path
     *
     * @return - server URL path
     */
    @Column(length = 2048)
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * Sets the URL path
     *
     * @param urlPath - server URL path
     */
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    /**
     * Gets the status
     *
     * @return - status of the server
     */
    @Enumerated
    @XmlTransient
    public RegistryNodeStatus getStatus() {
        return status;
    }

    /**
     * Sets the status
     *
     * @param status - status of the server
     */
    public void setStatus(RegistryNodeStatus status) {
        this.status = status;
    }

    /**
     * Gets the server directory
     *
     * @return - path of the server-side directory
     */
    @PlaceHolder(key = "serverDirectory")
    @Column(length = 2048)
    public String getServerDirectory() {
        return serverDirectory;
    }

    /**
     * Sets the server directory
     *
     * @param serverDirectory - path of the server-side directory
     */
    public void setServerDirectory(String serverDirectory) {
        this.serverDirectory = serverDirectory;
    }

    /**
     * Gets the type
     *
     * @return registry node type
     */
    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.FILESTORE;
    }

    /**
     * @param obj
     * @return
     */
    public boolean hasIdenticalData(Object obj) {
        if (obj instanceof FileStoreModel) {
            FileStoreModel item = (FileStoreModel) obj;
            if (getPath() != null && !getPath().equalsIgnoreCase(item.getPath())) {
                return false;
            }
            if (getName() != null && !getName().equalsIgnoreCase(item.getName())) {
                return false;
            }

            if (getPort() != null && !getPort().equals(item.getPort())) {
                return false;
            }
            if (getServerDirectory() != null && !getServerDirectory().equals(item.getServerDirectory())) {
                return false;
            }
            if (getServerName() != null && !getServerName().equals(item.getServerName())) {
                return false;
            }
            if (getUrlPath() != null && !getUrlPath().equals(item.getUrlPath())) {
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

}
