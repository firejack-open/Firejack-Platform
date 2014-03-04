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
