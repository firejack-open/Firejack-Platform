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