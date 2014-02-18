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
package net.firejack.platform.api.content.domain;

import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.api.registry.domain.Relationship;
import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@RuleSource("OPF.content.EntityResourceRelationship")
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class EntityResourceRelationship extends AbstractDTO {
	private static final long serialVersionUID = -3860154328657551700L;

	private Entity entity;
    private AbstractResource resource;
    private Relationship relationship;

    /**
     * @return
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * @param entity
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * @return
     */
    public AbstractResource getResource() {
        return resource;
    }

    /**
     * @param resource
     */
    public void setResource(AbstractResource resource) {
        this.resource = resource;
    }

    /**
     * @return
     */

    /**
     * @param relationship
     */
    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }
}
