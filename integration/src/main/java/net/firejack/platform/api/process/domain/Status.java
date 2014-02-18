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

package net.firejack.platform.api.process.domain;


import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.domain.ILookup;
import net.firejack.platform.core.validation.annotation.Length;
import net.firejack.platform.core.validation.annotation.NotBlank;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@RuleSource("OPF.process.Status")
public class Status extends BaseEntity implements ILookup {
    private static final long serialVersionUID = 1546043561208536140L;

    @Property
    private String name;
    @Property
    private String path;
    @Property
    private String lookup;
    @Property
    private String description;
    @Property
    private Integer sortPosition;

    @Override
    @NotBlank
    @Length(maxLength = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortPosition() {
        return sortPosition;
    }

    public void setSortPosition(Integer sortPosition) {
        this.sortPosition = sortPosition;
    }

}
