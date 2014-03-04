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

package net.firejack.platform.core.config.meta.element.process;


import net.firejack.platform.core.config.meta.element.PackageDescriptorElement;
import net.firejack.platform.core.model.registry.process.ProcessFieldModel;
import net.firejack.platform.core.utils.MiscUtils;

public class ProcessFieldElement extends PackageDescriptorElement<ProcessFieldModel> {

    private String displayName;
    private String format;
    private Integer orderPosition;
    private Boolean global;
    private String valueType;
    private EntityReference entityReference;
    private FieldReference fieldReference;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getOrderPosition() {
        return orderPosition;
    }

    public void setOrderPosition(Integer orderPosition) {
        this.orderPosition = orderPosition;
    }

    public Boolean getGlobal() {
        return global;
    }

    public void setGlobal(Boolean global) {
        this.global = global;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public EntityReference getEntityReference() {
        return entityReference;
    }

    public void setEntityReference(EntityReference entityReference) {
        this.entityReference = entityReference;
    }

    public FieldReference getFieldReference() {
        return fieldReference;
    }

    public void setFieldReference(FieldReference fieldReference) {
        this.fieldReference = fieldReference;
    }

    @Override
    public Class<ProcessFieldModel> getEntityClass() {
        return ProcessFieldModel.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ProcessFieldElement that = (ProcessFieldElement) o;

        return MiscUtils.objEquals(displayName, that.displayName) &&
                MiscUtils.objEquals(entityReference, that.entityReference) &&
                MiscUtils.objEquals(fieldReference, that.fieldReference) &&
                MiscUtils.objEquals(format, that.format) &&
                MiscUtils.objEquals(global, that.global) &&
                MiscUtils.objEquals(orderPosition, that.orderPosition) &&
                MiscUtils.objEquals(valueType, that.valueType);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (orderPosition != null ? orderPosition.hashCode() : 0);
        result = 31 * result + (global != null ? global.hashCode() : 0);
        result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
        result = 31 * result + (entityReference != null ? entityReference.hashCode() : 0);
        result = 31 * result + (fieldReference != null ? fieldReference.hashCode() : 0);
        return result;
    }
}