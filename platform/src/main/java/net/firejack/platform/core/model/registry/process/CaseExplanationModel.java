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

import net.firejack.platform.core.model.UIDModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;


@Entity
@Table(name = "opf_case_explanation")
public class CaseExplanationModel extends UIDModel {
    private static final long serialVersionUID = 821989076672189863L;

    private ProcessModel process;

    private String shortDescription;

    private String longDescription;


    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_process")
    @ForeignKey(name = "FK_PROCESS_CASE_EXPLANATION")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ProcessModel getProcess() {
        return process;
    }

    /**
     * @param process
     */
    public void setProcess(ProcessModel process) {
        this.process = process;
    }

    /**
     * @return
     */
    @Column(name = "short_description", length = 255)
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * @param shortDescription
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * @return
     */
    @Column(name = "long_description", columnDefinition = "MEDIUMTEXT")
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * @param longDescription
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

}
