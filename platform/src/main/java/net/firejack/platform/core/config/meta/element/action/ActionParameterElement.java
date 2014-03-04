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