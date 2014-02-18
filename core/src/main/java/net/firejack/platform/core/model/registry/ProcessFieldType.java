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