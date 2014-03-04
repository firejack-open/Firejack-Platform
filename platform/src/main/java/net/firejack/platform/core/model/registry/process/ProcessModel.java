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

import net.firejack.platform.core.model.registry.IAllowCreateAutoDescription;
import net.firejack.platform.core.model.registry.ProcessType;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;

import javax.persistence.*;
import java.util.List;

/**
 * Class represents process entity
 */
@Entity
@DiscriminatorValue("PCS")
public class ProcessModel extends RegistryNodeModel implements IAllowCreateAutoDescription {

	private static final long serialVersionUID = -2888230645739784750L;
    
	private List<ActivityModel> activities;

    private List<StatusModel> statuses;

    private List<CaseExplanationModel> caseExplanations;

    private List<ProcessFieldModel> processFields;

    private ProcessType processType;

    /**
     * Gets the type
     * @return - registry node type
     */
    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.PROCESS;
    }

    /**
     * Gets the activities
     * @return - list of the activities of the process
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy("sortPosition ASC")
    public List<ActivityModel> getActivities() {
        return activities;
    }

    /**
     * Sets the activities
     * @param activities - list of the activities of the process
     */
    public void setActivities(List<ActivityModel> activities) {
        this.activities = activities;
    }

    /**
     * Gets the statuses
     * @return - list of the statuses of the process
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy("sortPosition ASC")
    public List<StatusModel> getStatuses() {
        return statuses;
    }

    /**
     * Sets the statuses
     * @param statuses - list of the statuses of the process
     */
    public void setStatuses(List<StatusModel> statuses) {
        this.statuses = statuses;
    }

    /**
     * Gets the case explanations
     * @return - list of the explanations of the process cases
     */
    @OneToMany(mappedBy = "process", fetch = FetchType.LAZY)
    public List<CaseExplanationModel> getCaseExplanations() {
        return caseExplanations;
    }

    /**
     * Sets the case explanations
     * @param caseExplanations - list of the explanations of the process case
     */
    public void setCaseExplanations(List<CaseExplanationModel> caseExplanations) {
        this.caseExplanations = caseExplanations;
    }

    @OneToMany(mappedBy = "process", fetch = FetchType.LAZY)
    @OrderBy("orderPosition ASC")
    public List<ProcessFieldModel> getProcessFields() {
        return processFields;
    }

    public void setProcessFields(List<ProcessFieldModel> processFields) {
        this.processFields = processFields;
    }

    @Enumerated
    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }
}
