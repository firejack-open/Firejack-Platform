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

package net.firejack.platform.core.model.registry;


public enum ProcessFieldType {

    STRING ("stringValue"),

    INTEGER ("integerValue"),

    LONG ("longValue"),

    DOUBLE ("doubleValue"),

    BOOLEAN ("booleanValue"),

    DATE ("dateValue");

    // needed only for values of @DiscriminatorValue annotation
    public static final String STRING_CONST = "STRING";
    public static final String INTEGER_CONST = "INTEGER";
    public static final String LONG_CONST = "LONG";
    public static final String DOUBLE_CONST = "DOUBLE";
    public static final String BOOLEAN_CONST = "BOOLEAN";
    public static final String DATE_CONST = "DATE";

    private String columnName;

    ProcessFieldType(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}