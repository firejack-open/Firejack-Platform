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

package net.firejack.platform.api.site.domain;

import net.firejack.platform.api.authority.domain.Permission;
import net.firejack.platform.api.authority.domain.SecurityDriven;
import net.firejack.platform.api.content.domain.ImageResourceVersion;
import net.firejack.platform.api.content.domain.TextResourceVersion;
import net.firejack.platform.api.registry.model.RegistryNodeStatus;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.model.registry.EntityProtocol;
import net.firejack.platform.core.model.registry.NavigationElementType;
import net.firejack.platform.core.validation.annotation.*;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


@Component
@RuleSource("OPF.site.NavigationElement")
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NavigationElement extends Lookup implements SecurityDriven {
    private static final long serialVersionUID = -856481843397690489L;

    @Property
	private String serverName;
	@Property
	private Integer port;
	@Property
	private String parentPath;
	@Property
	private String urlPath;
    @Property
    private String urlParams;
    @Property
	private String pageUrl;
    @Property
    private Boolean hidden;
    @Property
    private NavigationElementType elementType;
	@Property
	private EntityProtocol protocol;
	@Property
	private RegistryNodeStatus status;
    @Property
    private List<Permission> permissions;
    private List<NavigationElement> navigationElements;
    private TextResourceVersion textResourceVersion;
    private ImageResourceVersion imageResourceVersion;
    @Property
    private Integer sortPosition;
    @Property
    private Lookup main;

    @Condition("serverNameMethodCondition")
	@Length(maxLength = 255)
    public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

    @Condition("portMethodCondition")
	@Match(expression = "^(|\\d{2,5})$")
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
    @Match(expression = "^(/[a-z0-9\\-_]+)+$", example = "/url/path/example")
	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

    @Length(maxLength = 2048)
    public String getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(String urlParams) {
        this.urlParams = urlParams;
    }

    @Length(maxLength = 2048)
    @Match(expression = "^([/\\.]?[a-z0-9\\-_]+)+$", example = "/url/path/example")
    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @EnumValue(enumClass = NavigationElementType.class, nullable = false)
    @DefaultValue("PAGE")
    public NavigationElementType getElementType() {
        return elementType;
    }

    public void setElementType(NavigationElementType elementType) {
        this.elementType = elementType;
    }

    @EnumValue(enumClass = EntityProtocol.class, nullable = true)
    @DefaultValue("HTTP")
	public EntityProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(EntityProtocol protocol) {
		this.protocol = protocol;
	}

    @NotNull
	@DefaultValue("UNKNOWN")
	public RegistryNodeStatus getStatus() {
		return status;
	}

	public void setStatus(RegistryNodeStatus status) {
		this.status = status;
	}

    public Integer getSortPosition() {
        return sortPosition;
    }

    public void setSortPosition(Integer sortPosition) {
        this.sortPosition = sortPosition;
    }

    public List<NavigationElement> getNavigationElements() {
        return navigationElements;
    }

    public void setNavigationElements(List<NavigationElement> navigationElements) {
        this.navigationElements = navigationElements;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public TextResourceVersion getTextResourceVersion() {
        return textResourceVersion;
    }

    public void setTextResourceVersion(TextResourceVersion textResourceVersion) {
        this.textResourceVersion = textResourceVersion;
    }

    public ImageResourceVersion getImageResourceVersion() {
        return imageResourceVersion;
    }

    public void setImageResourceVersion(ImageResourceVersion imageResourceVersion) {
        this.imageResourceVersion = imageResourceVersion;
    }

    public Lookup getMain() {
        return main;
    }

    public void setMain(Lookup main) {
        this.main = main;
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

}
