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

package net.firejack.platform.core.config.meta.element.action;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.core.config.meta.INamedPackageDescriptorElement;
import net.firejack.platform.core.model.registry.ParameterTransmissionType;

public class ActionParameterElement implements INamedPackageDescriptorElement {

    private String name;
    private String description;
    private String uid;
    private FieldType type;
    private ParameterTransmissionType location;
    private Integer orderPosition;

    /**
     * @return
     */
    public FieldType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(FieldType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    /**
     * @return
     */
    public Integer getOrderPosition() {
        return orderPosition;
    }

    /**
     * @param orderPosition
     */
    public void setOrderPosition(Integer orderPosition) {
        this.orderPosition = orderPosition;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public void setPath(String path) {

    }
}