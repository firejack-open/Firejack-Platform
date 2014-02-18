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

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.user.UserModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "opf_case_note")
public class CaseNoteModel extends BaseEntityModel {
    private static final long serialVersionUID = -7572556176350210555L;

    private CaseModel processCase;

    private TaskModel taskModel;

    private UserModel user;

    private String text;


    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_case")
    @ForeignKey(name = "FK_CASE_NOTE_CASE")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public CaseModel getProcessCase() {
        return processCase;
    }

    /**
     * @param processCase
     */
    public void setProcessCase(CaseModel processCase) {
        this.processCase = processCase;
    }

    /**
     * @return
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_task")
    @ForeignKey(name = "FK_CASE_NOTE_TASK")
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
    @JoinColumn(name = "id_user")
    @ForeignKey(name = "FK_CASE_NOTE_USER")
    public UserModel getUser() {
        return user;
    }

    /**
     * @param user
     */
    public void setUser(UserModel user) {
        this.user = user;
    }

    /**
     * @return
     */
    @Column(name = "text", columnDefinition = "MEDIUMTEXT")
    public String getText() {
        return text;
    }

    /**
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }
}
