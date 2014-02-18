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