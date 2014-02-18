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

package net.firejack.platform.api.deployment.domain;

import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/** Class represents filestore value object and is passed among broker and services */

@Component
@XmlRootElement
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class NavigationChanges extends AbstractDTO {
    private static final long serialVersionUID = 9186694789466525063L;

    @Property
    private List<String> lookup;
    @Property
    private Long timestamp;

    public List<String> getLookup() {
        return lookup;
    }

    public void setLookup(List<String> lookup) {
        this.lookup = lookup;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
