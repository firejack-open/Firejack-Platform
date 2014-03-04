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
