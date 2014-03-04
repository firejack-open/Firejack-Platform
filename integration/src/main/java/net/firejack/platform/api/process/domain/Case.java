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

import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Case extends BaseEntity {
    private static final long serialVersionUID = 6097356968389773543L;

    private boolean hasPreviousTask;
    private boolean userCanPerform;
    @Property
    private Process process;
    @Property
    private Status status;
    @Property
    private User assignee;
    @Property
    private List<CaseObject> caseObjects;
    @Property
    private String description;
    @Property
    private String data;
    @Property
    private Boolean active;
    @Property
    private Date startDate;
    @Property
    private Date updateDate;
    @Property
    private Date completeDate;
    @Property(name = "processFieldCaseValues")
    private List<ProcessFieldCaseValue> customFields;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public List<CaseObject> getCaseObjects() {
        return caseObjects;
    }

    public void setCaseObjects(List<CaseObject> caseObjects) {
        this.caseObjects = caseObjects;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasPreviousTask() {
        return hasPreviousTask;
    }

    public void setHasPreviousTask(boolean hasPreviousTask) {
        this.hasPreviousTask = hasPreviousTask;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isUserCanPerform() {
        return userCanPerform;
    }

    public void setUserCanPerform(boolean userCanPerform) {
        this.userCanPerform = userCanPerform;
    }

    public List<ProcessFieldCaseValue> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<ProcessFieldCaseValue> customFields) {
        this.customFields = customFields;
    }
}
