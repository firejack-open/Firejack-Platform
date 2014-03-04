/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
