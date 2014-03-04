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

package net.firejack.platform.core.config.meta.construct;

import net.firejack.platform.core.config.meta.IDomainElement;
import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IEntityElementContainer;
import net.firejack.platform.core.config.meta.INamedPackageDescriptorElement;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class DomainConfigElement extends BaseConfigElement implements IDomainElement, IEntityElementContainer {

    private List<IDomainElement> configuredDomains;
    private List<IEntityElement> configuredEntities;
    private INamedPackageDescriptorElement parent;
    private String prefix;
    private Boolean versionSubDomain;
    private Boolean dataSource;
    private String wsdlLocation;

    /**
     * @param name
     */
    public DomainConfigElement(String name) {
        super(name);
    }

    @Override
    public IDomainElement[] getConfiguredDomains() {
        return DiffUtils.getArray(configuredDomains, IDomainElement.class);
    }

    /**
     * @param domains
     */
    public void setConfiguredDomains(List<IDomainElement> domains) {
        this.configuredDomains = domains;
    }

    @Override
    public IEntityElement[] getConfiguredEntities() {
        return DiffUtils.getArray(configuredEntities, IEntityElement.class);
    }

    public void setConfiguredEntities(List<IEntityElement> configuredEntities) {
        this.configuredEntities = configuredEntities;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public INamedPackageDescriptorElement getParent() {
        return parent;
    }

    /**
     * @param parent
     */
    public void setParent(INamedPackageDescriptorElement parent) {
        this.parent = parent;
    }

    @Override
    public boolean isVersionSubDomain() {
        return versionSubDomain != null && versionSubDomain;
    }

    /**
     * @param versionSubDomain
     */
    public void setVersionSubDomain(Boolean versionSubDomain) {
        this.versionSubDomain = versionSubDomain;
    }

    public boolean isDataSource() {
        return dataSource != null && dataSource;
    }

    public void setDataSource(Boolean dataSource) {
        this.dataSource = dataSource;
    }

    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    /**
     * @param domain
     */
    public void add(IDomainElement domain) {
        if (domain != null) {
            if (configuredDomains == null) {
                configuredDomains = new ArrayList<IDomainElement>();
            }
            configuredDomains.add(domain);
        }
    }

    /**
     * @param entity
     */
    public void add(IEntityElement entity) {
        if (entity != null) {
            if (configuredEntities == null) {
                configuredEntities = new ArrayList<IEntityElement>();
            }
            configuredEntities.add(entity);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (o instanceof DomainConfigElement) {
            DomainConfigElement domain = (DomainConfigElement) o;
            return StringUtils.equals(this.uid, domain.uid) &&
                    StringUtils.equals(this.path, domain.path) &&
                    StringUtils.equals(this.name, domain.name);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return path + " : " + name;
    }
}