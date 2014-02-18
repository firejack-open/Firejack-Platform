package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.user.UserModel;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

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
