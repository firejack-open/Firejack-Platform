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

package net.firejack.platform.api.authority.domain;


import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.model.registry.authority.WildcardStyle;
import net.firejack.platform.core.validation.annotation.*;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Component
@XmlRootElement
@RuleSource("OPF.authority.ResourceLocation")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ResourceLocation extends Lookup implements SecurityDriven {
    private static final long serialVersionUID = 419238922636581972L;

    @Property
    private String serverName;
    @Property
    private Integer port;
    @Property
    private String parentPath;
    @Property
    private String urlPath;
    @Property
    private WildcardStyle wildcardStyle;
    @Property
    private List<Permission> permissions;


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
    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    @EnumValue(enumClass = WildcardStyle.class, nullable = true)
    @DefaultValue("ANT")
    public WildcardStyle getWildcardStyle() {
        return wildcardStyle;
    }

    public void setWildcardStyle(WildcardStyle wildcardStyle) {
        this.wildcardStyle = wildcardStyle;
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

}
