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

import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


/**
 * Class holds values of the parameters for operations upon cases such as perform, rollback or stopping the case
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CaseOperationsParams extends AbstractDTO {

    private Long caseId;
    private Long assigneeId;
    private Long explanationId;
    private Long taskId;
    private String noteText;
    private String taskDescription;
    private String processLookup;
    private String caseDescription;
    private boolean allowNullAssignee;
    private String entityType;
    private Long activityId;
    private List<CaseObject> caseObjects;
    private List<UserActor> userActors;
    private List<Long> entityIds;

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getExplanationId() {
        return explanationId;
    }

    public void setExplanationId(Long explanationId) {
        this.explanationId = explanationId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getProcessLookup() {
        return processLookup;
    }

    public void setProcessLookup(String processLookup) {
        this.processLookup = processLookup;
    }

    public List<CaseObject> getCaseObjects() {
        return caseObjects;
    }

    public void setCaseObjects(List<CaseObject> caseObjects) {
        this.caseObjects = caseObjects;
    }

    public List<UserActor> getUserActors() {
        return userActors;
    }

    public void setUserActors(List<UserActor> userActors) {
        this.userActors = userActors;
    }

    public String getCaseDescription() {
        return caseDescription;
    }

    public void setCaseDescription(String caseDescription) {
        this.caseDescription = caseDescription;
    }

    public boolean isAllowNullAssignee() {
        return allowNullAssignee;
    }

    public void setAllowNullAssignee(boolean allowNullAssignee) {
        this.allowNullAssignee = allowNullAssignee;
    }

    public Long getEntityId() {
        if (entityIds != null) {
            return entityIds.get(0);
        }
        return null;
    }

    public void setEntityId(Long entityId) {
        if (entityIds == null) {
            entityIds = new ArrayList<Long>();
        }
        entityIds.add(entityId);
    }

    public List<Long> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<Long> entityIds) {
        this.entityIds = entityIds;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

}