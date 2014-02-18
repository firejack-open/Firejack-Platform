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
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.validation.annotation.*;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Component
@XmlRootElement
@RuleSource("OPF.registry.Server")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Server extends RegistryNode {
	private static final long serialVersionUID = 7882884226879758381L;

	@Property
	private String serverName;
	@Property
	private Integer port;
	@Property
	private EntityProtocol protocol;
	@Property
	private HTTPMethod method;
	@Property
	private RegistryNodeStatus status;
	@Property
	private List<Package> associatedPackages;
    @Property
    private String publicKey;

    @Override
    @NotBlank
    @Length(maxLength = 255)
    public String getName() {
        return super.getName();
    }

	@NotBlank
	@Length(maxLength = 255)
	@Condition("editableServerNameMethodCondition")
    @Match(expression = "^(\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b|([a-z]+(\\.[a-z0-9\\-]{2,})*)|([a-z0-9\\-]+(\\.[a-z0-9\\-]{2,})+))$",
			example = "127.0.0.1 or localhost")
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@NotNull
	@DefaultValue("80")
    @LessThan(intVal = 65536, checkEquality = true)
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@NotNull
	@EnumValue(enumClass = EntityProtocol.class)
	@DefaultValue("HTTP")
	public EntityProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(EntityProtocol protocol) {
		this.protocol = protocol;
	}

	public HTTPMethod getMethod() {
		return method;
	}

	public void setMethod(HTTPMethod method) {
		this.method = method;
	}

	@NotNull
	@DefaultValue("UNKNOWN")
	public RegistryNodeStatus getStatus() {
		return status;
	}

	public void setStatus(RegistryNodeStatus status) {
		this.status = status;
	}

	public List<Package> getAssociatedPackages() {
		return associatedPackages;
	}

	public void setAssociatedPackages(List<Package> associatedPackages) {
		this.associatedPackages = associatedPackages;
	}

    @Length(maxLength = 1024)
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
