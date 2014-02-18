/**
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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
package net.firejack.platform.api.process.domain;

import net.firejack.platform.core.model.registry.ProcessFieldType;

import java.util.Date;

public class CustomFieldsSearchVO extends AbstractPaginatedSortableSearchTermVO {
	private static final long serialVersionUID = 7465345352605232271L;

	private Long fieldId;
    private String stringValue;
    private Integer integerValue;
    private Boolean booleanValue;
    private Long longValue;
    private Date dateValue;
    private Double doubleValue;
    private String valueType;

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Object getValue() {
        switch (ProcessFieldType.valueOf(valueType)) {
            case STRING:
                return stringValue;
            case INTEGER:
                return integerValue;
            case LONG:
                return longValue;
            case DOUBLE:
                return doubleValue;
            case BOOLEAN:
                return booleanValue;
            case DATE:
                return dateValue;
            default:
                return null;
        }
    }

    public String getValueColumn() {
        return ProcessFieldType.valueOf(valueType).getColumnName();
    }
}
