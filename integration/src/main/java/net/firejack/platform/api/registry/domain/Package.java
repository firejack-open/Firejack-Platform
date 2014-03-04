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

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.utils.VersionUtils;
import net.firejack.platform.core.validation.annotation.Length;
import net.firejack.platform.core.validation.annotation.Match;
import net.firejack.platform.core.validation.annotation.NotBlank;
import net.firejack.platform.core.validation.annotation.NotMatch;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@XmlRootElement
@RuleSource("OPF.registry.Package")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Package extends Lookup {
	private static final long serialVersionUID = -3714336848406943918L;
	@Property
	private String prefix;
	@Property
	private String serverName;
	@Property
	private Integer port;
    @Property
	private String urlPath;
	@Property
	private System system;
	@Property
	private Integer version;
    private String versionName;
	@Property
	private Integer databaseVersion;
    private String databaseVersionName;
	@Property
    private Database database;
    private PackageVersion packageVersion;
	private Boolean deployed;

    @Override
    @NotBlank
    @Length(maxLength = 255)
    @Match(expression = "^[a-z][a-z0-9]*$")
    @NotMatch(expression = "\\b(?:package|manager|platform)\\b", example = "'package' or 'manager' or 'platform'")
    public String getName() {
        return super.getName();
    }

	@Match(expression = "^[a-z0-9]+(_[a-z0-9]+)*$")
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
        this.versionName = VersionUtils.convertToVersion(getVersion());
	}

	public Integer getDatabaseVersion() {
		return databaseVersion;
	}

	public void setDatabaseVersion(Integer databaseVersion) {
		this.databaseVersion = databaseVersion;
        this.databaseVersionName = VersionUtils.convertToVersion(getDatabaseVersion());
	}

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public PackageVersion getPackageVersion() {
		return packageVersion;
	}

	public void setPackageVersion(PackageVersion packageVersion) {
		this.packageVersion = packageVersion;
	}

	public Boolean getDeployed() {
		return deployed;
	}

	public void setDeployed(Boolean deployed) {
		this.deployed = deployed;
	}

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

	@Length(maxLength = 2048)
    @Match(expression = "^/[a-z0-9]+$", example = "/example")
    @NotMatch(expression = "\\b(?:manager|platform)\\b", example = "'/manager' or '/platform'")
    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }
}
