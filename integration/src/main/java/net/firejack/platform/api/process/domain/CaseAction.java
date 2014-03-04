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
import net.firejack.platform.api.process.model.CaseActionType;
import net.firejack.platform.core.annotation.Property;
import net.firejack.platform.core.domain.BaseEntity;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CaseAction extends BaseEntity {
	private static final long serialVersionUID = 7223951255453652695L;

	@Property
    private CaseActionType type;

    @Property
    private String description;

    @Property(name = "case")
    private Case processCase;

    @Property
    private Task taskModel;

    @Property
    private CaseExplanation caseExplanation;

    @Property
    private CaseNote caseNote;

    @Property
    private Date performedOn;

    @Property
    private User user;

    public CaseActionType getType() {
        return type;
    }

    public void setType(CaseActionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Case getProcessCase() {
        return processCase;
    }

    public void setProcessCase(Case processCase) {
        this.processCase = processCase;
    }

    public Task getTaskModel() {
        return taskModel;
    }

    public void setTaskModel(Task taskModel) {
        this.taskModel = taskModel;
    }

    public CaseExplanation getCaseExplanation() {
        return caseExplanation;
    }

    public void setCaseExplanation(CaseExplanation caseExplanation) {
        this.caseExplanation = caseExplanation;
    }

    public CaseNote getCaseNote() {
        return caseNote;
    }

    public void setCaseNote(CaseNote caseNote) {
        this.caseNote = caseNote;
    }

    public Date getPerformedOn() {
        return performedOn;
    }

    public void setPerformedOn(Date performedOn) {
        this.performedOn = performedOn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
}
