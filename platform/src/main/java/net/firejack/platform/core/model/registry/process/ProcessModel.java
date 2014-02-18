/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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
