/*
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

package net.firejack.platform.api.directory.domain;


import net.firejack.platform.api.directory.model.DirectoryType;
import net.firejack.platform.api.registry.field.Field;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.validation.annotation.DefaultValue;
import net.firejack.platform.core.validation.annotation.EnumValue;
import net.firejack.platform.core.validation.annotation.Length;
import net.firejack.platform.core.validation.annotation.NotNull;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Component
@XmlRootElement
@RuleSource("OPF.directory.Directory")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Directory extends Lookup {
	private static final long serialVersionUID = -6702418358190401624L;

	@Property
	private Integer sortPosition;
	@Property
	private DirectoryType directoryType;
	@Property
	private RegistryNodeStatus status;
	@Property
	private String serverName;
	@Property
	private Integer port;
	@Property
	private String urlPath;
    @Property
    private String baseDN;
    @Property
    private String rootDN;
    @Property
    private String password;
    @Property
    private String ldapSchemaConfig;
	@Property
	@XmlElementWrapper(name = "fields")
	private List<Field> fields;
	private String directoryServiceTitle;

	public Directory() {
	}

	public Directory(String lookup, String directoryServiceTitle) {
		this.directoryServiceTitle = directoryServiceTitle;
		setLookup(lookup);
	}

	public Integer getSortPosition() {
		return sortPosition;
	}

	public void setSortPosition(Integer sortPosition) {
		this.sortPosition = sortPosition;
	}

	/**
	 * @return
	 */
	@NotNull
	@EnumValue(enumClass = DirectoryType.class)
	@DefaultValue("DATABASE")
	public DirectoryType getDirectoryType() {
		return directoryType;
	}

	/**
	 * @param type
	 */
	public void setDirectoryType(DirectoryType type) {
		this.directoryType = type;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@NotNull
	@EnumValue(enumClass = RegistryNodeStatus.class)
	@DefaultValue("UNKNOWN")
	public RegistryNodeStatus getStatus() {
		return status;
	}

	public void setStatus(RegistryNodeStatus status) {
		this.status = status;
	}

	@Length(maxLength = 1535)
	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getDirectoryServiceTitle() {
		return directoryServiceTitle;
	}

	public void setDirectoryServiceTitle(String directoryServiceTitle) {
		this.directoryServiceTitle = directoryServiceTitle;
	}

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    public String getRootDN() {
        return rootDN;
    }

    public void setRootDN(String rootDN) {
        this.rootDN = rootDN;
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