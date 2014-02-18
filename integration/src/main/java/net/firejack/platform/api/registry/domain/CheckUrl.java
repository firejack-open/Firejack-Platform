/**
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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
