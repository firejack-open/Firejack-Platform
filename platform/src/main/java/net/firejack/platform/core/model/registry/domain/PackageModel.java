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

import net.firejack.platform.core.annotation.PlaceHolder;
import net.firejack.platform.core.annotation.PlaceHolders;
import net.firejack.platform.core.model.registry.*;
import net.firejack.platform.core.model.registry.system.DatabaseModel;
import net.firejack.platform.core.utils.StringUtils;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;


@Entity
@DiscriminatorValue("PKG")
@PlaceHolders(name = "package", holders = {
        @PlaceHolder(key = "context.url", value = "{url}"),
		@PlaceHolder(key = "base.url", value = "http://{host}:{port}{url}")
})
public class PackageModel extends RegistryNodeModel implements
        IPrefixContainer, IDatabaseAssociated, IUrlPathContainer, IAllowDrag, IAllowDrop, IAllowCreateAutoDescription {

    private static final long serialVersionUID = 8873647207577824776L;
    private String prefix;
    private SystemModel system;
    private DatabaseModel database;
    private Integer version;
    private Integer databaseVersion;
	private String serverName;
	private Integer port;
    private String urlPath;

    @Override
    @Column(name = "prefix", length = 64)
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_system")
    @ForeignKey(name = "FK_SYSTEM_ASSOCIATION_PACKAGE")
    public SystemModel getSystem() {
        return system;
    }


    /**
     * @param system
     */
    public void setSystem(SystemModel system) {
        this.system = system;
    }

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_database")
	@ForeignKey(name = "FK_DATABASE_ASSOCIATION_PACKAGE")
	public DatabaseModel getDatabase() {
		return database;
	}

	public void setDatabase(DatabaseModel database) {
		this.database = database;
	}

	@Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.PACKAGE;
    }

    /**
     * @return
     */
    @Column(name = "version", nullable = false)
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return
     */
    @Column(name = "database_version", nullable = false)
    public Integer getDatabaseVersion() {
        return databaseVersion;
    }

    /**
     * @param databaseVersion
     */
    public void setDatabaseVersion(Integer databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    @Column(length = 2048)
    @PlaceHolder(key = "url")
    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

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

    @Override
    @Transient
    public Boolean getDataSource() {
        return null;
    }

    @Override
    @Transient
    public void setDataSource(Boolean dataSource) {
    }

    public String constructServerUrl() {
        StringBuilder sb = new StringBuilder();
        String sPort = "";
        RegistryNodeProtocol protocol = null;
        if (RegistryNodeProtocol.HTTP.getPort().equals(getPort())) {
            protocol = RegistryNodeProtocol.HTTP;
        } else if (RegistryNodeProtocol.HTTPS.getPort().equals(getPort())) {
            protocol = RegistryNodeProtocol.HTTPS;
        } else {
            sPort = ":" + getPort();
        }
        sb.append(protocol != null ? protocol.getProtocol() : RegistryNodeProtocol.HTTP.getProtocol());
        sb.append(getServerName());
        sb.append(sPort);
        if (StringUtils.isNotBlank(getUrlPath())) {
            if (!getUrlPath().startsWith("/")) {
                sb.append("/");
            }
            sb.append(getUrlPath().replaceAll("/+", "/"));
        }
        return sb.toString();
    }
}
