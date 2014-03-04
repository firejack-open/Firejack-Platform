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
