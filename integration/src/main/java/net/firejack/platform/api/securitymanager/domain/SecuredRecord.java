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

package net.firejack.platform.api.securitymanager.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SecuredRecord extends BaseEntity {
    private static final long serialVersionUID = -8855738457294327054L;

    @Property
    private String name;
    private String description;
    @Property
    private String paths;
    @Property(name = "registryNode.id")
    private Long registryNodeId;
    private String registryNodeLookup;
    @Property
    private Long externalNumberId;
    @Property
    private String externalStringId;
    private List<SecuredRecord> securedRecords;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public Long getRegistryNodeId() {
        return registryNodeId;
    }

    public void setRegistryNodeId(Long registryNodeId) {
        this.registryNodeId = registryNodeId;
    }

    public String getRegistryNodeLookup() {
        return registryNodeLookup;
    }

    public void setRegistryNodeLookup(String registryNodeLookup) {
        this.registryNodeLookup = registryNodeLookup;
    }

    public Long getExternalNumberId() {
        return externalNumberId;
    }

    public void setExternalNumberId(Long externalNumberId) {
        this.externalNumberId = externalNumberId;
    }

    public String getExternalStringId() {
        return externalStringId;
    }

    public void setExternalStringId(String externalStringId) {
        this.externalStringId = externalStringId;
    }

    @XmlElementWrapper(name = "securedRecords")
    public List<SecuredRecord> getSecuredRecords() {
        return securedRecords;
    }

    public void setSecuredRecords(List<SecuredRecord> securedRecords) {
        this.securedRecords = securedRecords;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SecuredRecord");
        sb.append("{name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", paths='").append(paths).append('\'');
        sb.append(", registryNodeId=").append(registryNodeId);
        sb.append(", registryNodeLookup='").append(registryNodeLookup).append('\'');
        sb.append(", externalNumberId=").append(externalNumberId);
        sb.append(", externalStringId='").append(externalStringId).append('\'');
        sb.append(", securedRecords=").append(securedRecords);
        sb.append('}');
        return sb.toString();
    }
}