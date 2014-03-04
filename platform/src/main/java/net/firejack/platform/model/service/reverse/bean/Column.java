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

package net.firejack.platform.model.service.reverse.bean;

import net.firejack.platform.api.registry.model.FieldType;

public class Column {

    private String name;
    private String dataType;
    private Integer length;
    private Integer decimalDigits;
    private Boolean nullable;
    private String defaultValue;
    private Boolean autoIncrement;
    private String characterSet;
    private String collate;
    private String comment;
    private FieldType fieldType;
    private Table table;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }

    public String getCollate() {
        return collate;
    }

    public void setCollate(String collate) {
        this.collate = collate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Column column = (Column) o;

        if (autoIncrement != null ? !autoIncrement.equals(column.autoIncrement) : column.autoIncrement != null)
            return false;
        if (characterSet != null ? !characterSet.equals(column.characterSet) : column.characterSet != null)
            return false;
        if (collate != null ? !collate.equals(column.collate) : column.collate != null) return false;
        if (comment != null ? !comment.equals(column.comment) : column.comment != null) return false;
        if (fieldType != null ? !fieldType.equals(column.fieldType) : column.fieldType != null) return false;
        if (dataType != null ? !dataType.equals(column.dataType) : column.dataType != null) return false;
        if (defaultValue != null ? !defaultValue.equals(column.defaultValue) : column.defaultValue != null)
            return false;
        if (length != null ? !length.equals(column.length) : column.length != null) return false;
        if (!name.equals(column.name)) return false;
        if (nullable != null ? !nullable.equals(column.nullable) : column.nullable != null) return false;
        if (table != null ? !table.equals(column.table) : column.table != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (length != null ? length.hashCode() : 0);
        result = 31 * result + (nullable != null ? nullable.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (autoIncrement != null ? autoIncrement.hashCode() : 0);
        result = 31 * result + (characterSet != null ? characterSet.hashCode() : 0);
        result = 31 * result + (collate != null ? collate.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (fieldType != null ? fieldType.hashCode() : 0);
        result = 31 * result + (table != null ? table.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Column{" +
                "table=" + table.getName() +
                ", name='" + name + '\'' +
                ", fieldType='" + fieldType.name() + '\'' +
                ", datatype='" + dataType + '\'' +
                ", length=" + length +
                ", nullable=" + nullable +
                ", defaultValue='" + defaultValue + '\'' +
                ", autoIncrement=" + autoIncrement +
                ", characterSet='" + characterSet + '\'' +
                ", collate='" + collate + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
