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
import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.model.registry.domain.PackageModel;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;


@Entity
@DiscriminatorValue("SRV")
public class ServerModel extends RegistryNodeModel implements IAllowCreateAutoDescription {

    private static final long serialVersionUID = -6628292592420012761L;
    private String serverName;
    private Integer port;
    private EntityProtocol protocol;
    private HTTPMethod method;
    private RegistryNodeStatus status;
    private List<PackageModel> associatedPackages;
    private String publicKey;
    private Boolean active;

    /**
     * @return
     */
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

    @Column(length = 1024)
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Column(name = "active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.SERVER;
    }

    /**
     * @return
     */
    @Transient
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
        if (obj instanceof ServerModel) {
            ServerModel item = (ServerModel) obj;
            if (getPath() != null && !getPath().equalsIgnoreCase(item.getPath())) {
                return false;
            }
            if (getName() != null && !getName().equalsIgnoreCase(item.getName())) {
                return false;
            }

            if (getPort() != null && !getPort().equals(item.getPort())) {
                return false;
            }
            if (getProtocol() != null && !getProtocol().equals(item.getProtocol())) {
                return false;
            }
            if (getServerName() != null && !getServerName().equals(item.getServerName())) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

}
