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

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

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
