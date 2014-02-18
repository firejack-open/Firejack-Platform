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
import net.firejack.platform.core.model.registry.process.ActivityModel;


public class ActivityElement extends PackageDescriptorElement<ActivityModel> {

    private String type;
    private String status;
    private String actor;
    private Integer order;
    private Boolean notify;

    /**
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return
     */
    public String getActor() {
        return actor;
    }

    /**
     * @param actor
     */
    public void setActor(String actor) {
        this.actor = actor;
    }

    /**
     * @return
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * @param order
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * @return
     */
    public Boolean getNotify() {
        return notify;
    }

    /**
     * @param notify
     */
    public void setNotify(Boolean notify) {
        this.notify = notify;
    }

    @Override
    public Class<ActivityModel> getEntityClass() {
        return ActivityModel.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ActivityElement that = (ActivityElement) o;

        if (actor != null ? !actor.equals(that.actor) : that.actor != null) return false;
        if (notify != null ? !notify.equals(that.notify) : that.notify != null) return false;
        if (order != null ? !order.equals(that.order) : that.order != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (actor != null ? actor.hashCode() : 0);
        result = 31 * result + (order != null ? order.hashCode() : 0);
        result = 31 * result + (notify != null ? notify.hashCode() : 0);
        return result;
    }

    /*@Override
         public boolean equals(Object o) {
             return super.equals(o);
         }*/
}
