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
