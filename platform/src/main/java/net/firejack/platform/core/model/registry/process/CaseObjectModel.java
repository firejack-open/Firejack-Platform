package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.user.UserModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

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
