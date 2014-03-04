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

package net.firejack.platform.api.registry.domain;

import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.model.registry.DatabaseName;
import net.firejack.platform.core.model.registry.RegistryNodeProtocol;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckUrl extends AbstractDTO {
	private static final long serialVersionUID = -5538981879420185215L;

	private RegistryNodeProtocol protocol;
	private Integer port;
	private String serverName;
	private String parentPath;
	private String urlPath;
	private String status;
	private DatabaseName rdbms;
	private String username;
	private String password;
    private String ldapSchemaConfig;

	public RegistryNodeProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(RegistryNodeProtocol protocol) {
		this.protocol = protocol;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public DatabaseName getRdbms() {
		return rdbms;
	}

	public void setRdbms(DatabaseName rdbms) {
		this.rdbms = rdbms;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public String getLdapSchemaConfig() {
        return ldapSchemaConfig;
    }

    public void setLdapSchemaConfig(String ldapSchemaConfig) {
        this.ldapSchemaConfig = ldapSchemaConfig;
    }
}
