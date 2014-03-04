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

package net.firejack.platform.core.model.registry.domain;

import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.annotation.PlaceHolder;
import net.firejack.platform.core.annotation.PlaceHolders;
import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.store.jaxb.PackageAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;


@javax.persistence.Entity
@DiscriminatorValue("SYS")
@PlaceHolders(name = "system")
public class SystemModel extends RegistryNodeModel implements IAllowDrop, IAllowDrag, IFolder, IAllowCreateAutoDescription {

    private static final long serialVersionUID = 1754651153716093181L;
    private String serverName;
    private Integer port;
    private EntityProtocol protocol;
    private HTTPMethod method;
    private RegistryNodeStatus status;

    private List<PackageModel> associatedPackages;

    /**
     * @return
     */
    @PlaceHolder(key = "port")
    public Integer getPort() {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return
     */
    @PlaceHolder(key = "host")
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

    /**
     * @return
     */
    @Enumerated
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
    @Enumerated
    public HTTPMethod getMethod() {
        return method;
    }

    /**
     * @param method
     */
    public void setMethod(HTTPMethod method) {
        this.method = method;
    }

    /**
     * @return
     */
    @Enumerated
    @XmlTransient
    public RegistryNodeStatus getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(RegistryNodeStatus status) {
        this.status = status;
    }


    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.SYSTEM;
    }

    /**
     * @return
     */
    @XmlElement(name = "package")
    @XmlElementWrapper(name = "packages")
    @XmlJavaTypeAdapter(PackageAdapter.class)
    @OneToMany(mappedBy = "system", fetch = FetchType.LAZY)
    public List<PackageModel> getAssociatedPackages() {
        return associatedPackages;
    }

    /**
     * @param associatedPackages
     */
    public void setAssociatedPackages(List<PackageModel> associatedPackages) {
        this.associatedPackages = associatedPackages;
    }

    /**
     * @param obj
     * @return
     */
    public boolean hasIdenticalData(Object obj) {
        if (obj instanceof SystemModel) {
            SystemModel system = (SystemModel) obj;
            if (getPath() != null && !getPath().equalsIgnoreCase(system.getPath())) {
                return false;
            }
            if (getName() != null && !getName().equalsIgnoreCase(system.getName())) {
                return false;
            }
            if (getPort() != null && !getPort().equals(system.getPort())) {
                return false;
            }
            if (getProtocol() != null && !getProtocol().equals(system.getProtocol())) {
                return false;
            }
            if (getServerName() != null && !getServerName().equals(system.getServerName())) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
