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


import net.firejack.platform.api.registry.domain.Entity;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.Lookup;
import net.firejack.platform.core.model.registry.ProcessType;
import net.firejack.platform.core.validation.annotation.Validate;
import net.firejack.platform.core.validation.constraint.RuleSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static net.firejack.platform.core.validation.annotation.DomainType.*;


@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@RuleSource("OPF.process.Process")
@Validate(type = PROCESS, parents = {DOMAIN}, unique = {PROCESS, ENTITY, REPORT, DOMAIN})
public class Process extends Lookup {
    private static final long serialVersionUID = 3319375507633067493L;

    private Boolean supportMultiActivities;
    private Entity entity;

    @Property
    private List<Activity> activities;

    @Property
    private List<Status> statuses;

    @Property(name = "caseExplanations")
    private List<CaseExplanation> explanations;

    @Property
    private List<ProcessField> processFields;

    @Property
    private ProcessType processType;

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<CaseExplanation> getExplanations() {
        return explanations;
    }

    public void setExplanations(List<CaseExplanation> explanations) {
        this.explanations = explanations;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public List<ProcessField> getProcessFields() {
        return processFields;
    }

    public void setProcessFields(List<ProcessField> processFields) {
        this.processFields = processFields;
    }

    public Boolean getSupportMultiActivities() {
        return supportMultiActivities;
    }

    public void setSupportMultiActivities(Boolean supportMultiActivities) {
        this.supportMultiActivities = supportMultiActivities;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }
}
