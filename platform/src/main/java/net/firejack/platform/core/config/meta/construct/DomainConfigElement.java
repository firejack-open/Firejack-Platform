/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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