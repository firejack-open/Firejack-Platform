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
import net.firejack.platform.core.model.registry.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Class represents registry node of the database type
 */
@Entity
@DiscriminatorValue("DTB")
@PlaceHolders(name = "db", holders = {
        @PlaceHolder(key = "db.{name}.type", value = "{type}"),
		@PlaceHolder(key = "db.{name}.domains", value = "{domains}"),
        @PlaceHolder(key = "db.{name}.url", value = "{url}"),
        @PlaceHolder(key = "db.{name}.username", value = "{username}"),
        @PlaceHolder(key = "db.{name}.password", value = "{password}")
})
public class DatabaseModel extends RegistryNodeModel implements IAllowDrag, IAllowCreateAutoDescription {

    private static final long serialVersionUID = -810125279755262638L;
    private String serverName;
    private Integer port;
    private String urlPath;
    private String parentPath;
    private DatabaseProtocol protocol;
    private String username;
    private String password;
    private RegistryNodeStatus status;
    private DatabaseName rdbms;
	private String domains;

    /**
     * Gets the port
     *
     * @return - port for the database
     */
    @PlaceHolder(key = "port")
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the port
     *
     * @param port - port for the database
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Gets the server name
     *
     * @return - name of the database server
     */
    @Column(length = 1024)
    @PlaceHolder(key = "host")
    public String getServerName() {
        return serverName;
    }

    /**
     * Sets the server name
     *
     * @param serverName - name of the database server
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Column(length = 255)
    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    /**
     * Gets the URL path
     *
     * @return - database URL path
     */
    @Column(length = 2048)
    @PlaceHolder(key = "schema")
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * Sets the URL path
     *
     * @param urlPath - database URL path
     */
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    /**
     * Gets the protocol
     *
     * @return - database protocol
     */
    @Enumerated
    public DatabaseProtocol getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol
     *
     * @param protocol - database protocol
     */
    public void setProtocol(DatabaseProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * Gets the username
     *
     * @return - database username
     */
    @Column(length = 1024)
    @PlaceHolder(key = "username")
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username
     *
     * @param username - database username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password
     *
     * @return - database password
     */
    @Column(length = 1024)
    @PlaceHolder(key = "password")
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password
     *
     * @param password - database password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the status
     *
     * @return registry node status
     */
    @Enumerated
    @XmlTransient
    public RegistryNodeStatus getStatus() {
        return status;
    }

    /**
     * Sets the status
     *
     * @param status - registry node status
     */
    public void setStatus(RegistryNodeStatus status) {
        this.status = status;
    }

    /**
     * Gets the type
     *
     * @return - registry node type
     */
    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.DATABASE;
    }

    /**
     * Gets RDBMS
     *
     * @return relational database management system
     */
    @Column(name = "rdbms")
    @Enumerated(EnumType.STRING)
    @PlaceHolder(key = "type")
    public DatabaseName getRdbms() {
        return rdbms;
    }

    /**
     * Sets RDBMS
     *
     * @param rdbms - relational database management system
     */
    public void setRdbms(DatabaseName rdbms) {
        this.rdbms = rdbms;
    }

	@Transient
	@PlaceHolder(key = "url")
	public String getUrl() {
		return rdbms.getDbSchemaUrlConnection(protocol, serverName, String.valueOf(port), parentPath, urlPath);
	}

	@Transient
	@PlaceHolder(key = "domains")
	public String getDomains() {
		return domains;
	}

	public void setDomains(String domains) {
		this.domains = domains;
	}

	public void addDomain(String lookup){
		domains+=";"+lookup;
	}

	/**
     * @param obj
     * @return
     */
    public boolean hasIdenticalData(Object obj) {
        if (obj instanceof DatabaseModel) {
            DatabaseModel item = (DatabaseModel) obj;
            if (getPath() != null && !getPath().equalsIgnoreCase(item.getPath())) {
                return false;
            }
            if (getName() != null && !getName().equalsIgnoreCase(item.getName())) {
                return false;
            }

            if (getPassword() != null && !getPassword().equals(item.getPassword())) {
                return false;
            }
            if (getRdbms() != null && !getRdbms().equals(item.getRdbms())) {
                return false;
            }
            if (getServerName() != null && !getServerName().equals(item.getServerName())) {
                return false;
            }
            if (getUrlPath() != null && !getUrlPath().equals(item.getUrlPath())) {
                return false;
            }
            if (getUsername() != null && !getUsername().equals(item.getUsername())) {
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

}
