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
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.transform.ResultTransformer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Class represents case entity
 */
@Entity
@Table(name = "opf_case")
public class CaseModel extends BaseEntityModel {
    
    private static final long serialVersionUID = 49774591115419124L;

    /**
     * Class used to transform a result set row (tuple array) that consists of (case, task, processFieldCaseValue...)
     * into one CaseModel (first element of the array) setting the Task and list of ProcessFieldCaseValue into it
     */
    public static ResultTransformer CASE_CUSTOM_FIELDS_TRANSFORMER = new BasicTransformerAdapter() {

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {

            CaseModel processCase = (CaseModel) tuple[0];
            TaskModel taskModel = (TaskModel) tuple[1];

            processCase.setTaskModels(Arrays.asList(taskModel));

            List<ProcessFieldCaseValue> processFieldCaseValues = new ArrayList<ProcessFieldCaseValue>(tuple.length - 2);

            processCase.setProcessFieldCaseValues(processFieldCaseValues);

            List<Object> additionalColumns = new ArrayList<Object>();

            for (int i = 2; i < tuple.length; i++) {
                if (tuple[i] != null && !(tuple[i] instanceof ProcessFieldCaseValue)) {
                    additionalColumns.add(tuple[i]);
                } else {
                    ProcessFieldCaseValue fieldValue = (ProcessFieldCaseValue) tuple[i];
                    processFieldCaseValues.add(fieldValue);
                }
            }

            Object result;
            if (additionalColumns.isEmpty()) {
                result = processCase;
            } else {
                Object[] resultData = new Object[additionalColumns.size() + 1];
                resultData[0] = processCase;
                int i = 1;
                for (Object additionalColumnValue : additionalColumns) {
                    resultData[i++] = additionalColumnValue;
                }
                result = resultData;
            }
            return result;
        }
    };

    private ProcessModel process;

    private String description;

    private String data;

    private Boolean active;

    private StatusModel status;

    private UserModel assignee;

    private Date startDate;

    private Date updateDate;

    private Date completeDate;

    private List<CaseObjectModel> caseObjectModels;

    private List<TaskModel> taskModels;

    private List<ProcessFieldCaseValue> processFieldCaseValues;

    /**
     * Gets the process
     * @return - process for the case
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_process")
    @ForeignKey(name = "FK_PROCESS_CASE")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ProcessModel getProcess() {
        return process;
    }

    /**
     * Sets the process
     * @param process - process for the case
     */
    public void setProcess(ProcessModel process) {
        this.process = process;
    }

    /**
     * Gets the description
     * @return - description of the case
     */
    @Column(length = 4096)
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description
     * @param description - description of the case
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the data
     * @return - case data
     */
    @Column(length = 4096)
    public String getData() {
        return data;
    }

    /**
     * Sets the data
     * @param data - case data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets active
     * @return - the flag showing whether the case is active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * Sets active
     * @param active - the flag showing whether the case is active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * Gets the status
     * @return - status of the case
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status")
    @ForeignKey(name = "FK_STATUS_CASE")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public StatusModel getStatus() {
        return status;
    }

    /**
     * Sets the status
     * @param status - status of the case
     */
    public void setStatus(StatusModel status) {
        this.status = status;
    }

    /**
     * Gets the assignee
     * @return - user who is assigned to the case
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_assignee")
    @ForeignKey(name = "FK_USER_CASE")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public UserModel getAssignee() {
        return assignee;
    }

    /**
     * Sets the assignee
     * @param assignee - user who is assigned to the case
     */
    public void setAssignee(UserModel assignee) {
        this.assignee = assignee;
    }

    /**
     * Gets the start date
     * @return - date when the case started
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date
     * @param startDate - date when the case started
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the update date
     * @return - date when the case has been updated
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets the update date
     * @param updateDate - date when the case has been updated
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * Gets the complete date
     * @return - date when the case has been completed
     */
    public Date getCompleteDate() {
        return completeDate;
    }

    /**
     * Sets the complete date
     * @param completeDate - date when the case has been completed
     */
    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    /**
     * Gets the case objects
     * @return - list of the objects for the case
     */
    @OneToMany(mappedBy = "case", fetch = FetchType.LAZY)
    public List<CaseObjectModel> getCaseObjectModels() {
        return caseObjectModels;
    }

    /**
     * Sets the case objects
     * @param caseObjectModels - list of the objects for the case
     */
    public void setCaseObjectModels(List<CaseObjectModel> caseObjectModels) {
        this.caseObjectModels = caseObjectModels;
    }

    /**
     * Gets the tasks
     * @return - list of the tasks of the case
     */
    @OneToMany(mappedBy = "case", fetch = FetchType.LAZY)
    public List<TaskModel> getTaskModels() {
        return taskModels;
    }

    /**
     * Sets the tasks
     * @param taskModels - list of the tasks of the case
     */
    public void setTaskModels(List<TaskModel> taskModels) {
        this.taskModels = taskModels;
    }

    /**
     * Gets the processFieldCaseValues
     * @return - list of the processFieldCaseValues of the case
     */
    @OneToMany(mappedBy = "case", fetch = FetchType.LAZY)
    public List<ProcessFieldCaseValue> getProcessFieldCaseValues() {
        return processFieldCaseValues;
    }

    /**
     * Sets the processFieldCaseValues
     * @param processFieldCaseValues - list of the processFieldCaseValues of the case
     */
    public void setProcessFieldCaseValues(List<ProcessFieldCaseValue> processFieldCaseValues) {
        this.processFieldCaseValues = processFieldCaseValues;
    }
    
}
