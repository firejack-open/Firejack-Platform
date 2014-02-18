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
