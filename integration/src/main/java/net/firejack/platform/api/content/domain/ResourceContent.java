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

package net.firejack.platform.api.content.domain;

import net.firejack.platform.api.content.model.ResourceType;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.model.registry.resource.Cultures;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlRootElement;

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ResourceContent extends AbstractDTO {
	private static final long serialVersionUID = -5479062518381856520L;

	private String lookup;
    private String country;
    private Cultures culture;
    private Long registryNodeId;
    private Long collectionId;
    private Long resourceVersionId;
    private Long resourceId;
    private String lookupSuffix;
    private String value;
    private ResourceType resourceType;
    private String resourceName;

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Cultures getCulture() {
        return culture;
    }

    public void setCulture(Cultures culture) {
        this.culture = culture;
    }

    public Long getRegistryNodeId() {
        return registryNodeId;
    }

    public void setRegistryNodeId(Long registryNodeId) {
        this.registryNodeId = registryNodeId;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public Long getResourceVersionId() {
        return resourceVersionId;
    }

    public void setResourceVersionId(Long resourceVersionId) {
        this.resourceVersionId = resourceVersionId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getLookupSuffix() {
        return lookupSuffix;
    }

    public void setLookupSuffix(String lookupSuffix) {
        this.lookupSuffix = lookupSuffix;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

}