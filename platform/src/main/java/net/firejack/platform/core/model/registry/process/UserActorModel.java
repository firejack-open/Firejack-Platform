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

/**
 * Class represents user actor entity
 */
@Entity
@Table(name = "opf_user_actor")
public class UserActorModel extends BaseEntityModel {

	private static final long serialVersionUID = -1258853873637798570L;
	private UserModel user;

    private ActorModel actor;

    private ProcessModel process;

    private CaseModel processCase;

    /**
     * Gets the user
     * @return user who belongs to the actor
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    @ForeignKey(name = "FK_USER_USER_ACTOR")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public UserModel getUser() {
        return user;
    }

    /**
     * Sets the user
     * @param user - user who belongs to the actor
     */
    public void setUser(UserModel user) {
        this.user = user;
    }

    /**
     * Gets the actor
     * @return - activity actor
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_actor")
    @ForeignKey(name = "FK_ACTOR_USER_ACTOR")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ActorModel getActor() {
        return actor;
    }

    /**
     * Sets the actor
     * @param actor - activity actor
     */
    public void setActor(ActorModel actor) {
        this.actor = actor;
    }

    /**
     * Gets the process
     * @return - process for the actor
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_process")
    @ForeignKey(name = "FK_PROCESS_USER_ACTOR")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ProcessModel getProcess() {
        return process;
    }

    /**
     * Sets the process
     * @param process - process for the actor
     */
    public void setProcess(ProcessModel process) {
        this.process = process;
    }

    /**
     * Gets the case
     * @return - case for the actor
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_case")
    @ForeignKey(name = "FK_CASE_USER_ACTOR")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public CaseModel getCase() {
        return processCase;
    }

    /**
     * Sets the case
     * @param processCase - case for the actor
     */
    public void setCase(CaseModel processCase) {
        this.processCase = processCase;
    }

}
