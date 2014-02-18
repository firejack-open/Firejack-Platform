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

package net.firejack.platform.api.securitymanager.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TreeNodeSecuredRecord extends AbstractDTO {
    private static final long serialVersionUID = 7209169138821971890L;

    @Property
    private Long id;
    @Property
    private String name;
    @Property(name = "registryNode.id")
    private Long registryNodeId;
    private String registryNodeLookup;
    @Property
    private Long externalNumberId;
    @Property
    private String externalStringId;
    private Long externalParentNumberId;
    private String externalParentStringId;
    private String parentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getExternalParentNumberId() {
        return externalParentNumberId;
    }

    public void setExternalParentNumberId(Long externalParentNumberId) {
        this.externalParentNumberId = externalParentNumberId;
    }

    public String getExternalParentStringId() {
        return externalParentStringId;
    }

    public void setExternalParentStringId(String externalParentStringId) {
        this.externalParentStringId = externalParentStringId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

}