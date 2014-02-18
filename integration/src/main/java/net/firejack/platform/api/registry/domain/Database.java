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

import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.model.registry.DatabaseName;
import net.firejack.platform.core.model.registry.DatabaseProtocol;
import net.firejack.platform.core.validation.annotation.*;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static net.firejack.platform.core.validation.annotation.DomainType.*;

@Component
@XmlRootElement
@RuleSource("OPF.registry.Database")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
@Validate(type = DATABASE, parents = {SYSTEM}, unique = {DATABASE, SERVER, FILESTORE})
public class Database extends RegistryNode {
	private static final long serialVersionUID = -6563687614642858635L;

	@Property
	private String serverName;
	@Property
	private Integer port;
	@Property
	private String urlPath;
    @Property
    private String parentPath;
	@Property
	private DatabaseProtocol protocol;
	@Property
	private String username;
	@Property
	private String password;
	@Property
	private RegistryNodeStatus status;
	@Property
	private DatabaseName rdbms;

	@NotBlank
	@Length(maxLength = 255)
    @DefaultValue("localhost")
    @Match(expression = "^(\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b|([a-z]+(\\.[a-z0-9]{2,})*)|([a-z0-9\\-]+(\\.[a-z0-9\\-]{2,})+))$")
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@NotNull
	@DefaultValue("3306")
    @LessThan(intVal = 65536, checkEquality = true)
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@NotBlank
	@Length(maxLength = 2048)
    @Match(expression = "^[A-Za-z0-9]+(_[A-Za-z0-9]+)*$", example = "'main' or 'db_main'")
    @NotMatch(expression = "\\b(mysql|test|performance_schema)\\b")
	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

    @Length(maxLength = 255)
    @Match(expression = "^[A-Za-z0-9]+(_[A-Za-z0-9]+)*$", example = "'main' or 'db_main'")
    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    @NotNull
	@EnumValue(enumClass = DatabaseProtocol.class)
	@DefaultValue("JDBC")
	public DatabaseProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(DatabaseProtocol protocol) {
		this.protocol = protocol;
	}

	@NotBlank
	@Length(maxLength = 64)
    @Match(expression = "^\\w+$", example = "'root' or 'user1'")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Length(maxLength = 64)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@NotNull
	@DefaultValue("UNKNOWN")
	public RegistryNodeStatus getStatus() {
		return status;
	}

	public void setStatus(RegistryNodeStatus status) {
		this.status = status;
	}

	@NotNull
	@EnumValue(enumClass = DatabaseName.class)
	@DefaultValue("MySQL")
	public DatabaseName getRdbms() {
		return rdbms;
	}

	public void setRdbms(DatabaseName rdbms) {
		this.rdbms = rdbms;
	}
}
