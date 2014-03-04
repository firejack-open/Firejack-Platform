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

import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.SecurityDriven;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.HTTPMethod;
import net.firejack.platform.core.validation.annotation.*;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


@Component
@XmlRootElement
@RuleSource("OPF.registry.Action")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Action extends Lookup implements SecurityDriven {
	private static final long serialVersionUID = 2375072114310199453L;

	@Property
	private String serverName;
	@Property
	private Integer port;
	@Property
	private String parentPath;
	@Property
	private String urlPath;
	@Property
	private EntityProtocol protocol;
	@Property
	private HTTPMethod method;
	@Property
	private RegistryNodeStatus status;

	@Property
	private Entity inputVOEntity;
	@Property
	private Lookup outputVOEntity;

	@Property
	private String soapUrlPath;
	@Property
	private String soapMethod;

	@Property
	@XmlElementWrapper(name = "parameters")
	private List<ActionParameter> actionParameters;
	@Property
	@XmlElementWrapper(name = "permissions")
	private List<Permission> permissions;

    @Override
    @NotBlank
    @Length(maxLength = 255)
    @Match(expression = "^[a-z]([a-z0-9]*-?[a-z0-9]+)*$", msgKey = "validation.parameter.action.name.should.match.exp")
    public String getName() {
        return super.getName();
    }

	@Condition("serverNameMethodCondition")
	@Length(maxLength = 255)
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Condition("portMethodCondition")
	@Match(expression = "^\\d{2,5}$")
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Condition("parentPathMethodCondition")
	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	@Condition("pathMethodCondition")
	@Length(maxLength = 2048)
    @Match(expression = "^(/[a-z0-9\\-]+)+$", example = "/url/path/example")
	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
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

	@NotNull
	@EnumValue(enumClass = HTTPMethod.class)
	@DefaultValue("GET")
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

	public Entity getInputVOEntity() {
		return inputVOEntity;
	}

	public void setInputVOEntity(Entity inputVOEntity) {
		this.inputVOEntity = inputVOEntity;
	}

	public Lookup getOutputVOEntity() {
		return outputVOEntity;
	}

	public void setOutputVOEntity(Lookup outputVOEntity) {
		this.outputVOEntity = outputVOEntity;
	}

	public String getSoapUrlPath() {
		return soapUrlPath;
	}

	public void setSoapUrlPath(String soapUrlPath) {
		this.soapUrlPath = soapUrlPath;
	}

    @NotBlank
	@Length(maxLength = 255)
    @Match(expression = "^[a-z][a-zA-Z0-9]*$", example = "'create' or 'readByLookup'")
	public String getSoapMethod() {
		return soapMethod;
	}

	public void setSoapMethod(String soapMethod) {
		this.soapMethod = soapMethod;
	}

	public List<ActionParameter> getActionParameters() {
		return actionParameters;
	}

	public void setActionParameters(List<ActionParameter> actionParameters) {
		this.actionParameters = actionParameters;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public List<String> produceRequiredPermissionList() {
        List<String> permissionLookupList = new ArrayList<String>();
        if (permissions != null) {
            for (Permission permission : permissions) {
                permissionLookupList.add(permission.getLookup());
            }
        }
		return permissionLookupList;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Action action = (Action) o;

		if (actionParameters != null ? !actionParameters.equals(action.actionParameters) : action.actionParameters != null)
			return false;
		if (method != action.method) return false;
		if (parentPath != null ? !parentPath.equals(action.parentPath) : action.parentPath != null) return false;
		if (port != null ? !port.equals(action.port) : action.port != null) return false;
		if (protocol != action.protocol) return false;
		if (serverName != null ? !serverName.equals(action.serverName) : action.serverName != null) return false;
		if (soapMethod != null ? !soapMethod.equals(action.soapMethod) : action.soapMethod != null) return false;
		if (soapUrlPath != null ? !soapUrlPath.equals(action.soapUrlPath) : action.soapUrlPath != null) return false;
		if (status != action.status) return false;
		return !(urlPath != null ? !urlPath.equals(action.urlPath) : action.urlPath != null);

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (serverName != null ? serverName.hashCode() : 0);
		result = 31 * result + (port != null ? port.hashCode() : 0);
		result = 31 * result + (parentPath != null ? parentPath.hashCode() : 0);
		result = 31 * result + (urlPath != null ? urlPath.hashCode() : 0);
		result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
		result = 31 * result + (method != null ? method.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		result = 31 * result + (soapUrlPath != null ? soapUrlPath.hashCode() : 0);
		result = 31 * result + (soapMethod != null ? soapMethod.hashCode() : 0);
		result = 31 * result + (actionParameters != null ? actionParameters.hashCode() : 0);
		return result;
	}
}