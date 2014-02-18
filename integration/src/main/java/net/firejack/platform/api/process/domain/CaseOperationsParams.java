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