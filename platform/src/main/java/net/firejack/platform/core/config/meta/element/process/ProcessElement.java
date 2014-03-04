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

package net.firejack.platform.core.config.meta.element.process;

import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.model.registry.ProcessType;
import net.firejack.platform.core.model.registry.process.ProcessModel;
import net.firejack.platform.core.utils.MiscUtils;

import java.util.List;


public class ProcessElement extends PackageDescriptorElement<ProcessModel> {

    private List<ActivityElement> activities;
    private List<StatusElement> statuses;
    private List<ExplanationElement> explanations;
    private List<ProcessFieldElement> customFields;
    private ProcessType processType;

    /**
     * @return
     */
    public List<ActivityElement> getActivities() {
        return activities;
    }

    /**
     * @param activities
     */
    public void setActivities(List<ActivityElement> activities) {
        this.activities = activities;
    }

    /**
     * @return
     */
    public List<StatusElement> getStatuses() {
        return statuses;
    }

    /**
     * @param statuses
     */
    public void setStatuses(List<StatusElement> statuses) {
        this.statuses = statuses;
    }

    /**
     * @return
     */
    public List<ExplanationElement> getExplanations() {
        return explanations;
    }

    /**
     * @param explanations
     */
    public void setExplanations(List<ExplanationElement> explanations) {
        this.explanations = explanations;
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }

    @Override
    public Class<ProcessModel> getEntityClass() {
        return ProcessModel.class;
    }

    public List<ProcessFieldElement> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<ProcessFieldElement> customFields) {
        this.customFields = customFields;
    }

    @Override
    public boolean equals(Object o) {
        ProcessElement process = (ProcessElement) o;
        return super.equals(o) &&
                ((this.getProcessType() == null && process.getProcessType() == null) ||
                 (this.getProcessType() != null && process.getProcessType() != null && this.getProcessType().equals(process.getProcessType()))) &&
                MiscUtils.elementEquals(this.getActivities(), process.getActivities()) &&
                MiscUtils.elementEquals(this.getExplanations(), process.getExplanations()) &&
                MiscUtils.elementEquals(this.getStatuses(), process.getStatuses()) &&
                MiscUtils.elementEquals(this.getCustomFields(), process.getCustomFields());
    }
}