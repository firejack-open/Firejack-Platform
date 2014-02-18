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

package net.firejack.platform.api.process.domain;


import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.domain.ILookup;
import net.firejack.platform.core.validation.annotation.*;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Class represents activity value object and is passed among brokers and services
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@RuleSource("OPF.process.Activity")
public class Activity extends BaseEntity implements ILookup {
    private static final long serialVersionUID = 8227094537468282439L;

    @Property
    private Actor actor;
    @Property
    private Status status;
    @Property
    private ActivityType activityType;
    @Property
    private String name;
    @Property
    private String path;
    @Property
    private String lookup;
    @Property
    private String description;
    @Property
    private Boolean notify;
    @Property
    private ActivityOrder activityOrder;
    @Property
    private ActivityForm activityForm;
    @Property
    private Integer sortPosition;

    private List<ActivityAction> activityActions;

    @Property
    private List<ActivityField> fields;

    @NotNull
    @EnumValue(enumClass = ActivityType.class)
    @DefaultValue("HUMAN")
    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    @NotBlank
    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    @Override
//    @Length(maxLength = 3072)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @Length(maxLength = 1024)
    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

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
    @Length(maxLength = 1024)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @NotBlank
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getNotify() {
        return notify;
    }

    public void setNotify(Boolean notify) {
        this.notify = notify;
    }

    @NotNull
    public Integer getSortPosition() {
        return sortPosition;
    }

    public void setSortPosition(Integer sortPosition) {
        this.sortPosition = sortPosition;
    }

    @NotNull
    public ActivityOrder getActivityOrder() {
        return activityOrder;
    }

    public void setActivityOrder(ActivityOrder activityOrder) {
        this.activityOrder = activityOrder;
    }

    @NotNull
    public ActivityForm getActivityForm() {
        return activityForm;
    }

    public void setActivityForm(ActivityForm activityForm) {
        this.activityForm = activityForm;
    }

    public List<ActivityAction> getActivityActions() {
        return activityActions;
    }

    public void setActivityActions(List<ActivityAction> activityActions) {
        this.activityActions = activityActions;
    }

    public List<ActivityField> getFields() {
        return fields;
    }

    public void setFields(List<ActivityField> fields) {
        this.fields = fields;
    }
}
