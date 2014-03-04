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
