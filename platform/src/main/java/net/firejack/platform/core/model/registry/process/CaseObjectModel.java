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

package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.user.UserModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

/**
 * Class represents case object entity
 */
@Entity
@Table(name = "opf_case_object")
public class CaseObjectModel extends BaseEntityModel {

	private static final long serialVersionUID = -2923511997596995795L;
	private CaseModel processCase;

    private TaskModel task;

    private Long entityId;

    private String entityType;

    private StatusModel status;

    private Date updateDate;

    private UserModel createdBy;

    private UserModel updatedBy;

    /**
     * Gets the case
     * @return - case to which the object is related
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_case")
    @ForeignKey(name = "FK_CASE_OBJECT_CASE")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public CaseModel getCase() {
        return processCase;
    }

    /**
     * Sets the case
     * @param processCase - case to which the object is related
     */
    public void setCase(CaseModel processCase) {
        this.processCase = processCase;
    }

    /**
     * Gets the task
     * @return - task of the case
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_task")
    @ForeignKey(name = "FK_CASE_OBJECT_TASK")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public TaskModel getTask() {
        return task;
    }

    /**
     * Sets the task
     * @param task - task of the case
     */
    public void setTask(TaskModel task) {
        this.task = task;
    }

    /**
     * Gets the entity ID
     * @return - ID of the entity
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * Sets the entity ID
     * @param entityId - ID if the entity
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets the entity type
     * @return - type of the entity
     */
    @Column(length = 2048)
    public String getEntityType() {
        return entityType;
    }

    /**
     * Sets the entity type
     * @param entityType - type of the entity
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * Gets the status
     * @return - case status
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_status")
    @ForeignKey(name = "FK_CASE_OBJECT_STATUS")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public StatusModel getStatus() {
        return status;
    }

    /**
     * Sets the status
     * @param status - case status
     */
    public void setStatus(StatusModel status) {
        this.status = status;
    }

    /**
     * Gets the update date
     * @return - date of the last update
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets the update date
     * @param updateDate - date of the last update
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * Gets created by
     * @return - user who has created the case object relation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_created_by")
    @ForeignKey(name = "FK_CASE_OBJECT_USER_CREATED")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public UserModel getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets created by
     * @param createdBy - user who has created the case object relation
     */
    public void setCreatedBy(UserModel createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets updated by
     * @return - user who has updated the case object relation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_updated_by")
    @ForeignKey(name = "FK_CASE_OBJECT_USER_UPDATED")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public UserModel getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the updated by
     * @param updatedBy - user who has updated the case object relation
     */
    public void setUpdatedBy(UserModel updatedBy) {
        this.updatedBy = updatedBy;
    }
    
}
