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

import net.firejack.platform.api.registry.field.Field;
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
@RuleSource("OPF.content.FieldResource")
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class FieldResource extends AbstractDTO {
	private static final long serialVersionUID = 1243287958307743702L;

	private Field field;
    private AbstractResource resource;

    /**
     * @return
     */
    public Field getField() {
        return field;
    }

    /**
     * @param field
     */
    public void setField(Field field) {
        this.field = field;
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
}