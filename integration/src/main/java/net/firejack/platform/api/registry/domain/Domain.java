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
import net.firejack.platform.core.validation.annotation.*;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static net.firejack.platform.core.validation.annotation.DomainType.DOMAIN;
import static net.firejack.platform.core.validation.annotation.DomainType.PACKAGE;

@Component
@XmlRootElement
@RuleSource("OPF.registry.Domain")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
@Validate(type = DOMAIN, parents = {PACKAGE}, unique = {DOMAIN})
public class Domain extends Lookup {
	private static final long serialVersionUID = 7087212049880342817L;

	@Property
	private String prefix;
    @Property
    private Boolean dataSource;
    @Property
    private Database database;
    @Property
    private String wsdlLocation;
    @Property
    private Boolean xaSupport;

    @Match(expression = "^[a-z0-9]+(_[a-z0-9]+)*$")
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

    @NotNull
    public Boolean getDataSource() {
        return dataSource;
    }

    public void setDataSource(Boolean dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @NotBlank
    @Length(maxLength = 255)
    @Match(expression = "^[a-z][a-z0-9]*$", msgKey = "validation.parameter.domain.name.should.match.exp")
    @NotMatch(expression = "\\b(?:package|login|home|controller|model|view|admin|user|guest|system)\\b", example = "'package' or another")
    public String getName() {
        return super.getName();
    }

    @Condition("databaseMethodCondition")
    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    @NotNull
    public Boolean getXaSupport() {
        return xaSupport;
    }

    public void setXaSupport(Boolean xaSupport) {
        this.xaSupport = xaSupport;
    }
}
