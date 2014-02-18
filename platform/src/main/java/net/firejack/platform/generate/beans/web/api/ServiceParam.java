package net.firejack.platform.generate.beans.web.api;

import net.firejack.platform.core.model.registry.ParameterTransmissionType;
import net.firejack.platform.generate.beans.web.store.Param;

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

public class ServiceParam extends Param<ServiceParam> {
    private Integer order;
    private ParameterTransmissionType location;

    /***/
    public ServiceParam() {
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
    public ParameterTransmissionType getLocation() {
        return location;
    }

    /**
     * @param location
     */
    public void setLocation(ParameterTransmissionType location) {
        this.location = location;
    }

    @Override
    public int compareTo(ServiceParam param) {
        return this.order.compareTo(param.getOrder());
    }
}
