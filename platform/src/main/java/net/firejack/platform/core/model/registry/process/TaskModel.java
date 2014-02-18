package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.user.UserModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.transform.ResultTransformer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * Class represents task entity
 */
@Entity
@Table(name = "opf_task")
public class TaskModel extends BaseEntityModel {

	private static final long serialVersionUID = -4159237642082871111L;

    /**
     * A transformer that transforms a row (task, customField1, customField2, ...) into one task, placing all the customFields in the task's case property processFieldCaseValues
     */
    public static ResultTransformer TASK_CUSTOM_FIELDS_TRANSFORMER = new BasicTransformerAdapter() {

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            TaskModel taskModel = (TaskModel) tuple[0];

            List<ProcessFieldCaseValue> processFieldCaseValues = new ArrayList<ProcessFieldCaseValue>(tuple.length - 1);

            taskModel.getCase().setProcessFieldCaseValues(processFieldCaseValues);
            List<Object> additionalColumns = new ArrayList<Object>();
            for (int i = 1; i < tuple.length; i++) {
                if (tuple[i] != null && !(tuple[i] instanceof ProcessFieldCaseValue)) {
                    additionalColumns.add(tuple[i]);
                } else {
                    ProcessFieldCaseValue fieldValue = (ProcessFieldCaseValue) tuple[i];
                    processFieldCaseValues.add(fieldValue);
                }
            }
            Object result;
            if (additionalColumns.isEmpty()) {
                result = taskModel;
            } else {
                Object[] resultData = new Object[additionalColumns.size() + 1];
                resultData[0] = taskModel;
                int i = 1;
                for (Object additionalColumnValue : additionalColumns) {
                    resultData[i++] = additionalColumnValue;
                }
                result = resultData;
            }
            return result;
        }
    };

	private CaseModel processCase;

    private ActivityModel activity;

    private UserModel assignee;

    private Boolean active;

    private String description;

    private Date updateDate;

    private Date closeDate;

    /**
     * Gets the case
     * @return case the task belongs to
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_case")
    @ForeignKey(name = "FK_CASE_TASK")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public CaseModel getCase() {
        return processCase;
    }

    /**
     * Sets the case
     * @param processCase - case the task belongs to
     */
    public void setCase(CaseModel processCase) {
        this.processCase = processCase;
    }

    /**
     * Gets the activity
     * @return - activity for the task
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_activity")
    @ForeignKey(name = "FK_ACTIVITY_TASK")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ActivityModel getActivity() {
        return activity;
    }

    /**
     * Sets the activity
     * @param activity - activity for the task
     */
    public void setActivity(ActivityModel activity) {
        this.activity = activity;
    }

    /**
     * Gets the assignee
     * @return - user who is assigned to the task
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_assignee")
    @ForeignKey(name = "FK_USER_TASK")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public UserModel getAssignee() {
        return assignee;
    }

    /**
     * Sets the assignee
     * @param assignee - user who is assigned to the task
     */
    public void setAssignee(UserModel assignee) {
        this.assignee = assignee;
    }

    /**
     * Gets active
     * @return - the flag showing if the task is active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * Sets active
     * @param active - the flag showing if the task is active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Gets the description
     * @return - description of the task
     */
    @Column(length = 4096)
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description
     * @param description - description of the task
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets update date
     * @return - date when the task has been updated
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets update date
     * @param updateDate - date when the task has been updated
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * Gets close date
     * @return - date when the task has been closed
     */
    public Date getCloseDate() {
        return closeDate;
    }

    /**
     * Sets close date
     * @param closeDate - date when the task has been closed
     */
    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

}
