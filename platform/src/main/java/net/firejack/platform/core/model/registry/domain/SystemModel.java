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
