/*
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

package net.firejack.platform.model.service.reverse.analyzer;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.model.service.reverse.bean.Column;
import net.firejack.platform.model.service.reverse.bean.Table;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLTableAnalyzer extends AbstractTableAnalyzer {

    public MySQLTableAnalyzer(DataSource dataSource, String schema) {
        super(dataSource, schema);
    }

    @Override
    public Column createColumn(ResultSet rs, Table table) throws SQLException {
        Column column = new Column();
        column.setName(rs.getString("COLUMN_NAME"));
        column.setDataType(rs.getString("TYPE_NAME"));
        column.setLength(rs.getInt("COLUMN_SIZE"));
        column.setAutoIncrement(rs.getBoolean("IS_AUTOINCREMENT"));
        column.setNullable(rs.getBoolean("NULLABLE"));
        column.setComment(rs.getString("REMARKS"));
        String defaultValue = rs.getString("COLUMN_DEF");
        column.setDefaultValue(defaultValue);
        FieldType fieldType = detectFieldType(column);
        column.setFieldType(fieldType);
        column.setTable(table);
        return column;
    }

    private FieldType detectFieldType(Column column) {
        String dataType = column.getDataType();
        Integer length = column.getLength();
        String defaultValue = column.getDefaultValue();
        FieldType fieldType = FieldType.OBJECT;
        if (dataType.matches("(?i)(TINYINT|SMALLINT|MEDIUMINT|INT(EGER)?)(\\sUNSIGNED)?")) {
            fieldType = FieldType.INTEGER_NUMBER;
        } else if (dataType.matches("(?i)BIGINT(\\sUNSIGNED)?")) {
            fieldType = FieldType.LARGE_NUMBER;
        } else if (dataType.matches("(?i)(FLOAT|DOUBLE|PRECISION|REAL)(\\sUNSIGNED)?")) {
            fieldType = FieldType.DECIMAL_NUMBER;
        } else if (dataType.matches("(?i)(DECIMAL|NUMERIC)")) {
            fieldType = FieldType.CURRENCY;
        } else if (dataType.matches("(?i)(CHAR|VARCHAR)")) {
            if (length <= 16) {
                fieldType = FieldType.CODE;
            } else if (length <= 64) {
                fieldType = FieldType.TINY_TEXT;
            } else if (length <= 255) {
                fieldType = FieldType.SHORT_TEXT;
            } else if (length <= 1024) {
                fieldType = FieldType.MEDIUM_TEXT;
            } else if (length <= 2048) {
                fieldType = FieldType.URL;
            } else if (length <= 4096) {
                fieldType = FieldType.DESCRIPTION;
            } else {
                fieldType = FieldType.LONG_TEXT;
            }
        } else if (dataType.matches("(?i)TINY(TEXT|BLOB)")) {
            fieldType = FieldType.SHORT_TEXT;
        } else if (dataType.matches("(?i)(MEDIUM)?(TEXT|BLOB)")) {
            fieldType = FieldType.LONG_TEXT;
        } else if (dataType.matches("(?i)LONG(TEXT|BLOB)")) {
            fieldType = FieldType.UNLIMITED_TEXT;
        } else if (dataType.matches("(?i)DATE")) {
            fieldType = FieldType.DATE;
        } else if (dataType.matches("(?i)TIME")) {
            fieldType = FieldType.TIME;
        } else if (dataType.matches("(?i)TIMESTAMP")) {
            if ("CURRENT_TIMESTAMP".equalsIgnoreCase(defaultValue)) {
                fieldType = FieldType.CREATION_TIME;
            } else {
                fieldType = FieldType.UPDATE_TIME;
            }
        } else if (dataType.matches("(?i)DATETIME")) {
            fieldType = FieldType.EVENT_TIME;
        } else if (dataType.matches("(?i)(BIT|BOOL)")) {
            fieldType = FieldType.FLAG;
        } else if (dataType.matches("(?i)(ENUM|SET|YEAR)")) {
            fieldType = FieldType.CODE;
        }
        return fieldType;
    }

}
