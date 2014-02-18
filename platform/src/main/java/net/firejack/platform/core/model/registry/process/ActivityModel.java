package net.firejack.platform.core.model.registry.process;

import net.firejack.platform.api.process.domain.ActivityForm;
import net.firejack.platform.api.process.domain.ActivityOrder;
import net.firejack.platform.api.process.domain.ActivityType;
import net.firejack.platform.core.annotation.Lookup;
import net.firejack.platform.core.model.registry.ISortable;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.RegistryNodeModel;
import net.firejack.platform.core.model.registry.RegistryNodeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

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
 * Class represents activity entity
 */
@Entity
@Lookup(parent = RegistryNodeModel.class)
@Table(name = "opf_activity")
public class ActivityModel extends LookupModel<ProcessModel> implements ISortable {
    private static final long serialVersionUID = -2049707346490673859L;

    private ActorModel actor;
    private StatusModel status;
    private ActivityType activityType;
    private ActivityOrder activityOrder;
    private ActivityForm activityForm;
    private Integer sortPosition;
    private List<TaskModel> taskModels;
    private Boolean isNotify = true;
    private List<ActivityFieldModel> fields;

    /**
     * Gets the actor
     *
     * @return - actor of the activity
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_actor")
    @ForeignKey(name = "FK_ACTOR_ACTIVITY")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public ActorModel getActor() {
        return actor;
    }

    /**
     * Sets the actor
     *
     * @param actor - actor of the activity
     */
    public void setActor(ActorModel actor) {
        this.actor = actor;
    }

    /**
     * Gets the status
     *
     * @return - status of the activity
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status")
    @ForeignKey(name = "FK_STATUS_ACTIVITY")
    @OnDelete(action = OnDeleteAction.CASCADE)
    public StatusModel getStatus() {
        return status;
    }

    /**
     * Sets the status
     *
     * @param status - status of the activity
     */
    public void setStatus(StatusModel status) {
        this.status = status;
    }

    /**
     * Gets the type
     *
     * @return type of the activity
     */
    @Enumerated
    @Column(name = "type")
    public ActivityType getActivityType() {
        return activityType;
    }

    /**
     * @param activityType
     */
    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    /**
     * Gets the order position
     *
     * @return - position of the activity within the process
     */
    @Column(name = "order_position", nullable = false, columnDefinition = "INTEGER UNSIGNED DEFAULT 0")
    public Integer getSortPosition() {
        return sortPosition;
    }

    /**
     * Sets the order position
     *
     * @param sortPosition - position of the activity within the process
     */
    public void setSortPosition(Integer sortPosition) {
        this.sortPosition = sortPosition;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "activity_order")
    public ActivityOrder getActivityOrder() {
        return activityOrder;
    }

    public void setActivityOrder(ActivityOrder activityOrder) {
        this.activityOrder = activityOrder;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "activity_form")
    public ActivityForm getActivityForm() {
        return activityForm;
    }

    public void setActivityForm(ActivityForm activityForm) {
        this.activityForm = activityForm;
    }

    /**
     * Gets the tasks
     *
     * @return - list of the tasks to perform the activity
     */
    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY)
    public List<TaskModel> getTaskModels() {
        return taskModels;
    }

    /**
     * Sets the tasks
     *
     * @param taskModels - list of the tasks to perform the activity
     */
    public void setTaskModels(List<TaskModel> taskModels) {
        this.taskModels = taskModels;
    }

    /**
     * Gets notify
     *
     * @return - flag showing the notification settings
     */
    @Column(nullable = false)
    public Boolean getNotify() {
        return isNotify;
    }

    /**
     * Sets notify
     *
     * @param notify - flag showing the notification settings
     */
    public void setNotify(Boolean notify) {
        isNotify = notify;
    }

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL/*, orphanRemoval = true*/)
    @OnDelete(action = OnDeleteAction.CASCADE)
    public List<ActivityFieldModel> getFields() {
        return fields;
    }

    public void setFields(List<ActivityFieldModel> fields) {
        this.fields = fields;
    }

    /**
     * Gets the type
     *
     * @return registry node type
     */
    @Override
    @Transient
    public RegistryNodeType getType() {
        return RegistryNodeType.ACTIVITY;
    }
}
