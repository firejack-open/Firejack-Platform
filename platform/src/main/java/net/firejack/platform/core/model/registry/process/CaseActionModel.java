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

import net.firejack.platform.api.process.model.CaseActionType;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.user.UserModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "opf_case_action")
public class CaseActionModel extends BaseEntityModel {
    private static final long serialVersionUID = -7090447502199942110L;

    private CaseActionType type;

    private String description;

    private CaseModel processCase;

    private TaskModel taskModel;

    private CaseExplanationModel caseExplanation;

    private CaseNoteModel caseNote;

    private Date performedOn;

    private UserModel user;


    /**
     * @return
     */
    @Enumerated(EnumType.ORDINAL)
    public CaseActionType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(CaseActionType type) {
        this.type = type;
    }

    /**
     * @return
     */
    @Column(columnDefinition = "MEDIUMTEXT")
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_case", nullable = false)
    @ForeignKey(name = "FK_CASE_ACTION_CASE")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public CaseModel getCase() {
        return processCase;
    }

    /**
     * @param processCase
     */
    public void setCase(CaseModel processCase) {
        this.processCase = processCase;
    }

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_task", nullable = false)
    @ForeignKey(name = "FK_CASE_ACTION_TASK")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public TaskModel getTaskModel() {
        return taskModel;
    }

    /**
     * @param taskModel
     */
    public void setTaskModel(TaskModel taskModel) {
        this.taskModel = taskModel;
    }

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_case_explanation", nullable = true)
    @ForeignKey(name = "FK_CASE_ACTION_CASE_EXPLANATION")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public CaseExplanationModel getCaseExplanation() {
        return caseExplanation;
    }

    /**
     * @param caseExplanation
     */
    public void setCaseExplanation(CaseExplanationModel caseExplanation) {
        this.caseExplanation = caseExplanation;
    }

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_case_note", nullable = true)
    @ForeignKey(name = "FK_CASE_ACTION_CASE_NOTE")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public CaseNoteModel getCaseNote() {
        return caseNote;
    }

    /**
     * @param caseNote
     */
    public void setCaseNote(CaseNoteModel caseNote) {
        this.caseNote = caseNote;
    }

    /**
     * @return
     */
    public Date getPerformedOn() {
        return performedOn;
    }

    /**
     * @param performedOn
     */
    public void setPerformedOn(Date performedOn) {
        this.performedOn = performedOn;
    }

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = true)
    @ForeignKey(name = "FK_CASE_ACTION_USER")
    public UserModel getUser() {
        return user;
    }

    /**
     * @param user
     */
    public void setUser(UserModel user) {
        this.user = user;
    }

}
