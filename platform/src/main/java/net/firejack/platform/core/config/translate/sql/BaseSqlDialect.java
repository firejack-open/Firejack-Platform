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

package net.firejack.platform.core.config.translate.sql;


import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.construct.CompoundKeyColumnsRule;
import net.firejack.platform.core.config.meta.construct.CompoundKeyParticipantColumn;
import net.firejack.platform.core.config.meta.expr.*;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.config.translate.SqlTranslationResult;
import net.firejack.platform.core.config.translate.sql.exception.SqlProcessingException;
import net.firejack.platform.core.config.translate.sql.token.SqlToken;
import net.firejack.platform.core.utils.IHasName;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

public abstract class BaseSqlDialect implements ISqlDialect {

    protected static final String TOKEN_SPACE = " ";
    protected static final String TOKEN_DOUBLE_SPACE = "  ";
    protected static final String TOKEN_COMMA_SPACE = ", ";
    //protected static final String TOKEN_SEMICOLON = ";";
    protected static final String TOKEN_SEMICOLON_EOL = ";\n";
    protected static final String TOKEN_COMMA_EOL = ",\n";
    protected static final String TOKEN_BRACE_EOL = "(\n";
    protected static final String TOKEN_DEFAULT_SPACE = "DEFAULT ";
    protected static final String TOKEN_CREATE_TABLE_SPACE = "CREATE TABLE ";
    protected static final String TOKEN_RENAME_TABLE_SPACE = "RENAME TABLE ";
    protected static final String TOKEN_RENAME_TABLE_SPACE1 = " TO ";
    protected static final String TOKEN_WHERE = "WHERE";
    protected static final String TOKEN_DELETE_FROM = "DELETE FROM ";
    protected static final String TOKEN_ADD_COLUMN = " ADD COLUMN ";
    protected static final String TOKEN_OPEN_BRACE = " (";
    protected static final String TOKEN_CLOSE_BRACE = ")";

    protected static final String TOKEN_NULL = "NULL";
    protected static final String TOKEN_IS_NULL = " IS NULL";
    protected static final String TOKEN_IS_NOT_NULL = " IS NOT NULL";
    protected static final String TOKEN_NOT_NULL_SPACE = "NOT NULL ";
    protected static final String TOKEN_NULL_SPACE = "NULL ";

    protected static final String TOKEN_IF = "IF ";
    /*protected static final String TOKEN_BEGIN = "BEGIN\n";
    protected static final String TOKEN_END = "END\n";*/

    protected static final String TOKEN_EXP_NOT = "NOT ";
    protected static final String TOKEN_EXP_AND = "and";
    protected static final String TOKEN_EXP_OR = "or";
    protected static final String TOKEN_EXP_XOR = "xor";
    protected static final String TOKEN_EXP_IN = " in (";
    protected static final String TOKEN_EXP_MAX = "max(";
    protected static final String TOKEN_EXP_MIN = "min(";

    protected static final String TOKEN_ADD_CONSTRAINT = " ADD CONSTRAINT ";
    protected static final String TOKEN_DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
    protected static final String TOKEN_PRIMARY_KEY = " PRIMARY KEY (";
    protected static final String TOKEN_ALTER_TABLE = "ALTER TABLE ";
    protected static final String TOKEN_DROP_COLUMN = "DROP COLUMN ";
    protected static final String TOKEN_UNIQUE1 = "  UNIQUE `";
    protected static final String TOKEN_UNIQUE2 = "` ";
    protected static final String TOKEN_INSERT = "INSERT INTO ";
    protected static final String TOKEN_FOREIGN_KEY = " FOREIGN KEY ";
    protected static final String TOKEN_REFERENCES = " REFERENCES ";
    protected static final String TOKEN_ID = "id";

    protected static final String TOKEN_ON_DELETE = " ON DELETE ";
    protected static final String TOKEN_ON_UPDATE = " ON UPDATE ";
    protected static final String TOKEN_CASCADE = "CASCADE";
    protected static final String TOKEN_SET_NULL = "SET NULL";
    protected static final String TOKEN_NO_ACTION = "NO ACTION";

    protected static final String TOKEN_ADD_INDEX = "ADD INDEX ";

    protected static final String MSG_CANT_PROCESS_VALUE_OF_TYPE1 = "Can't process value of type [";
    protected static final String MSG_FAILED_TO_CALCULATE_TABLE_NAME = "Failed to calculate table name.";
    protected static final String MSG_NULL_NOT_ALLOWED = "Null value does not allowed.";
    protected static final String MSG_COULD_NOT_PROCESS_NULL_FUNCTION = "Could not process function which is null.";

    protected static final String MSG_WRONG_REFERENCE_FIELD_NAME = "Wrong reference field name specified.";
    protected static final String MSG_FIELD_TYPE_PARAMETER_IS_NULL = "Field type parameter should not be null";
    protected static final String MSG_FAILED_TO_EVAL_EXPR = "Failed to evaluate expression.";


    private static final Logger logger = Logger.getLogger(BaseSqlDialect.class);

    protected ISqlNameResolver sqlNameResolver;

    protected ISqlNameResolver getSqlNamesResolver() {
        return sqlNameResolver;
    }

    void setSqlNamesResolver(ISqlNameResolver sqlNameResolver) {
        this.sqlNameResolver = sqlNameResolver;
    }

    @Override
    public void initializeScript(SqlTranslationResult resultState) {
    }

    @Override
    public String createTable(IEntityElement entity, boolean createIdField) {
        String tableName = wrapWithSqlQuote(getTableName(entity));
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_CREATE_TABLE_SPACE).append(tableName)
                .append(TOKEN_BRACE_EOL);

        IFieldElement[] fields = entity.getFields();
        if (fields != null) {
            boolean idFound = false;
            String idFieldName = getSqlNamesResolver().resolveIdColumn();
            List<IFieldElement> ordinalFields = new ArrayList<IFieldElement>();
            for (IFieldElement field : fields) {
                if (idFieldName.equalsIgnoreCase(field.getName())) {
                    idFound = true;
                } else {
                    ordinalFields.add(field);
                }
            }
            IFieldElement[] fieldsToProcess = ordinalFields.toArray(new IFieldElement[ordinalFields.size()]);
            if (idFound) {
                constructTableBodyDefinition(sb, idFieldName, entity, fieldsToProcess);
            } else {
                addFieldDefinitions(tableName, sb, false, fieldsToProcess);
            }
        }
        CompoundKeyColumnsRule[] ukRules = entity.getCompoundKeyColumnsRules();
        if (ukRules != null) {
            for (CompoundKeyColumnsRule rule : ukRules) {
                if (rule.isDetermined()) {
                    sb.append(TOKEN_COMMA_EOL).append(uniqueEntry(rule));
                }
            }
        }
        sb.append("\n)");
        return sb.toString();
    }

    @Override
    public String addUniqueKey(String keyName, String tableWithUniqueKeys, String ukParticipant, String... ukParticipants) {
        Set<String> ukColumns;
        if (ukParticipants.length == 0) {
            ukColumns = new HashSet<String>();
            ukColumns.add(ukParticipant);
            Collections.addAll(ukColumns, ukParticipants);
        } else {
            ukColumns = DiffUtils.singleton(ukParticipant);
        }
        return addUniqueKey(keyName, tableWithUniqueKeys, ukColumns);
    }

    @Override
    public String createStagingTable(IEntityElement entity, String refField1, String refField2) throws SqlProcessingException {
        IFieldElement[] fields = entity.getFields();
        if (DiffUtils.findNamedElement(fields, refField1) == null ||
                DiffUtils.findNamedElement(fields, refField2) == null) {
            throw new IllegalArgumentException(MSG_WRONG_REFERENCE_FIELD_NAME);
        }
        String tableName = wrapWithSqlQuote(getTableName(entity));
        refField1 = wrapWithSqlQuote(refField1);
        refField2 = wrapWithSqlQuote(refField2);
        StringBuilder sb = new StringBuilder();
//        sb.append(TOKEN_CREATE_TABLE_SPACE).append(entity.getName()).append(TOKEN_BRACE_EOL);
        sb.append(TOKEN_CREATE_TABLE_SPACE).append(tableName).append(TOKEN_BRACE_EOL);
        addFieldDefinitions(tableName, sb, true, fields);
        sb.append(TOKEN_PRIMARY_KEY)
                .append(refField1).append(TOKEN_COMMA_SPACE)
                .append(refField2)
                .append(')').append("\n)");
        return sb.toString();
    }

    @Override
    public String modifyTableName(IEntityElement oldEntity, IEntityElement newEntity) throws SqlProcessingException {
        String oldName = wrapWithSqlQuote(getTableName(oldEntity));
        String newName = wrapWithSqlQuote(getTableName(newEntity));
        return modifyTableName(oldName, newName);
    }

    @Override
    public String modifyTableName(String oldTableName, String newTableName) throws SqlProcessingException {
        StringBuilder sb = new StringBuilder(TOKEN_RENAME_TABLE_SPACE);
        sb.append(oldTableName).append(TOKEN_RENAME_TABLE_SPACE1).append(newTableName);
        return sb.toString();
    }

    @Override
    public String dropTable(IEntityElement entity) {
        String tableName = wrapWithSqlQuote(getTableName(entity));
        return dropTable(tableName);
    }

    @Override
    public String dropTable(String entityName) throws SqlProcessingException {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_DROP_TABLE_IF_EXISTS).append(entityName).append(';');
        return sb.toString();
    }

    @Override
    public String addColumn(IEntityElement entity, IFieldElement field) {
        String tableName = wrapWithSqlQuote(getTableName(entity));
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_ALTER_TABLE).append(tableName)
                .append(TOKEN_ADD_COLUMN).append(populateColumnDefinition(tableName, field));
        return sb.toString();
    }

    @Override
    public String dropColumn(String entityName, String columnName) throws SqlProcessingException {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_ALTER_TABLE).append(entityName).append(TOKEN_SPACE)
                .append(TOKEN_DROP_COLUMN).append(columnName);
        return sb.toString();
    }

    @Override
    public String dropColumn(IEntityElement entity, IFieldElement field) {
        String tableName = wrapWithSqlQuote(getTableName(entity));
        return dropColumn(tableName, getSqlNamesResolver().resolveColumnName(field));
    }

    @Override
    public String delete(IEntityElement entity, IExpression<String> whereCondition)
            throws SqlProcessingException {
        String tableName = wrapWithSqlQuote(getTableName(entity));
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_DELETE_FROM).append(tableName);
        return processWhereClause(sb, whereCondition).toString();
    }

    @Override
    public String insertWithSelect(IEntityElement entity, String[] columnNames,
                                   String selectStatement) throws SqlProcessingException {
        SqlToken token = new SqlToken();
        String tableName = wrapWithSqlQuote(getTableName(entity));
        token.append(TOKEN_INSERT).append(tableName)
                .space().valuesInBraces(columnNames).space()
                .append(selectStatement);
        return token.statement();
    }

    @Override
    public String in(String target, String[] enumerableRanges) {
        StringBuilder sb = new StringBuilder();
        sb.append(target).append(TOKEN_EXP_IN);
        elementsList(sb, false, enumerableRanges)
                .append(')');
        return sb.toString();
    }

    @Override
    public String getTableName(IEntityElement entity) {
        String tableName = getSqlNamesResolver().resolveTableName(entity);
        if (StringUtils.isBlank(tableName)) {
            throw new SqlProcessingException(MSG_FAILED_TO_CALCULATE_TABLE_NAME);
        }
        return tableName;
    }

    @Override
    public String eq(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, '=');
    }

    @Override
    public String notEq(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, "<>");
    }

    @Override
    public String and(String expr1, String expr2) {
        StringBuilder sb = new StringBuilder();
        sb.append(expr1).append(TOKEN_SPACE)
                .append(TOKEN_EXP_AND).append(TOKEN_SPACE)
                .append(expr2);
        return sb.toString();
    }

    @Override
    public String or(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, TOKEN_EXP_OR);
    }

    @Override
    public String xor(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, TOKEN_EXP_XOR);
    }

    @Override
    public String greaterThan(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, '>');
    }

    @Override
    public String greaterOrEqualThan(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, ">=");
    }

    @Override
    public String lessThan(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, '<');
    }

    @Override
    public String lessOrEqualThan(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, "<=");
    }

    @Override
    public String add(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, '+');
    }

    @Override
    public String subtract(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, '-');
    }

    @Override
    public String multiply(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, '*');
    }

    @Override
    public String divide(String expr1, String expr2) {
        return binaryExpr(expr1, expr2, '/');
    }

    @Override
    public String not(String expr) {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_EXP_NOT).append(expr);
        return sb.toString();
    }

    @Override
    public String processExpression(IExpression<String> conditionExpression) {
        if (conditionExpression == null) {
            return "";
        }
        ExpressionFactory<String> factory = ExpressionFactory.getSqlExpressionInstance(this);
        IExpressionContext<String> context = factory.buildExpressionContext();
        try {
            return conditionExpression.evaluate(context);
        } catch (ExpressionEvaluationException e) {
            logger.error(MSG_FAILED_TO_EVAL_EXPR);
            throw new SqlProcessingException(e);
        }
    }

    @Override
    public String isNull(String expr) {
        StringBuilder sb = new StringBuilder();
        sb.append(expr).append(TOKEN_IS_NULL);
        return sb.toString();
    }

    @Override
    public String isNotNull(String expr) {
        StringBuilder sb = new StringBuilder();
        sb.append(expr).append(TOKEN_IS_NOT_NULL);
        return sb.toString();
    }

    @Override
    public String wrapWithBraces(String expr) {
        StringBuilder sb = new StringBuilder();
        sb.append('(').append(expr).append(')');
        return sb.toString();
    }

    @Override
    public String identifier(IHasName parentEntity, IFieldElement field, String alias) {
        if (StringUtils.isBlank(alias)) {
            StringBuilder sb = new StringBuilder();
            sb.append(parentEntity.getName())
                    .append('.').append(field.getName());
            return sb.toString();
        }
        return alias;
    }

    @Override
    public String function(ExpressionFunctions function, Object... parameters) {
        if (function == null) {
            throw new IllegalArgumentException(MSG_COULD_NOT_PROCESS_NULL_FUNCTION);
        }
        StringBuilder sb = new StringBuilder();
        switch (function) {
            case max:
                sb.append(TOKEN_EXP_MAX);
                elementsList(sb, false, parameters)
                        .append(')');
                return sb.toString();
            case min:
                sb.append(TOKEN_EXP_MIN);
                elementsList(sb, false, parameters)
                        .append(')');
        }
        return sb.toString();
    }

    @Override
    public String value(Object value) {
        StringBuilder sb = new StringBuilder();
        if (value == null) {
            sb.append(TOKEN_NULL);
        } else if (value instanceof String || value instanceof Character) {
            sb.append('\'').append(String.valueOf(value)).append('\'');
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(String.valueOf(value));
        } else {
            throw new IllegalArgumentException(
                    (new StringBuilder(MSG_CANT_PROCESS_VALUE_OF_TYPE1)).append(value.getClass())
                            .append(']').toString());
        }
        return sb.toString();
    }

    protected void constructTableBodyDefinition(
            StringBuilder sb, String idFieldName, IEntityElement entity,
            IFieldElement[] fieldsToProcess) {
        String tableName = getSqlNamesResolver().resolveTableName(entity);
        sb.append(TOKEN_DOUBLE_SPACE)
                .append(wrapWithSqlQuote(idFieldName))
                .append(getTokenIdDefinition());
        addFieldDefinitions(tableName, sb, false, fieldsToProcess);
    }

    protected String binaryExpr(String expr1, String expr2, String operator) {
        StringBuilder sb = new StringBuilder();
        sb.append(expr1).append(TOKEN_SPACE)
                .append(operator).append(TOKEN_SPACE)
                .append(expr2);
        return sb.toString();
    }

    protected String binaryExpr(String expr1, String expr2, char operator) {
        StringBuilder sb = new StringBuilder();
        sb.append(expr1).append(TOKEN_SPACE)
                .append(operator).append(TOKEN_SPACE)
                .append(expr2);
        return sb.toString();
    }

    protected StringBuilder elementsList(StringBuilder sb, boolean acceptNulls, Object... parameters) {
        if (parameters.length == 0) {
            return sb;
        } else {
            processValue(sb, acceptNulls, parameters[0]);
            for (int i = 1; i < parameters.length; i++) {
                processValue(sb.append(TOKEN_COMMA_SPACE), acceptNulls, parameters[i]);
            }
            return sb;
        }
    }

    protected StringBuilder processValue(StringBuilder sb, boolean acceptNull, Object value) {
        if (value == null) {
            if (acceptNull) {
                sb.append(TOKEN_NULL);
            } else {
                throw new IllegalArgumentException(MSG_NULL_NOT_ALLOWED);
            }
        } else if (value instanceof String || value instanceof Character) {
            sb.append('\'').append(String.valueOf(value)).append('\'');
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(String.valueOf(value));
        } else {
            throw new IllegalArgumentException(
                    (new StringBuilder(MSG_CANT_PROCESS_VALUE_OF_TYPE1)).append(value.getClass())
                            .append(']').toString());
        }
        return sb;
    }

    protected String wrapWithSqlQuote(String name) {
        return StringUtils.wrapWith("`", name);
    }

    protected StringBuilder addFieldDefinitions(String tableName, StringBuilder sb, boolean finishWithComma, IFieldElement... fields) {
        if (fields != null && fields.length > 0) {
            for (IFieldElement field : fields) {
                sb.append(TOKEN_DOUBLE_SPACE).append(populateColumnDefinition(tableName, field));
                if (field != fields[fields.length - 1] || finishWithComma) {
                    sb.append(TOKEN_COMMA_EOL);
                }
            }
        }
        return sb;
    }

    protected StringBuilder processWhereClause(StringBuilder sb, IExpression<String> whereCondition) {
        if (whereCondition == null) {
            return sb;
        }
        String condition = processExpression(whereCondition);
        if (StringUtils.isBlank(condition)) {
            return sb;
        }
        return sb.append(TOKEN_SPACE).append(TOKEN_WHERE).append(TOKEN_SPACE).append(condition);
    }

    protected String populateColumnDefinition(String tableName, IFieldElement field) {
        StringBuilder sb = new StringBuilder();
        String fieldName = getSqlNamesResolver().resolveColumnName(field);
        String columnType = getColumnType(field.getType());
        sb.append(wrapWithSqlQuote(fieldName)).append(TOKEN_SPACE)
                .append(columnType).append(TOKEN_SPACE)
                .append(field.isRequired() ? TOKEN_NOT_NULL_SPACE : TOKEN_NULL_SPACE);
        if (!columnType.toUpperCase().contains(" DEFAULT ")) {
            renderDefaultValue(sb, field);
        }
        return sb.toString();
    }

    protected void renderDefaultValue(StringBuilder sb, IFieldElement field) {
        if (field.getDefaultValue() != null && field.getType() != null) {
            sb.append(TOKEN_DEFAULT_SPACE);
            if (field.getType().isBoolean()) {
                sb.append(String.valueOf(field.getDefaultValue()).equalsIgnoreCase("true") ? 1 : 0);
            } else if (field.getType().isString()) {
                sb.append('\'').append(field.getDefaultValue()).append('\'');
            } else {
                sb.append(field.getDefaultValue());
            }
            sb.append(TOKEN_SPACE);
        }
    }

    protected boolean objEquals(Object obj1, Object obj2) {
        return DiffUtils.objEquals(obj1, obj2);
    }

    protected String uniqueEntry(CompoundKeyColumnsRule compoundKeyColumnsRule) {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_UNIQUE1).append(
                compoundKeyColumnsRule.getName()).append(TOKEN_UNIQUE2)
                .append('(');
        int i = 0;
        CompoundKeyParticipantColumn[] columns =
                compoundKeyColumnsRule.getCompoundKeyParticipantColumns();
        for (CompoundKeyParticipantColumn column : columns) {
            sb.append(column.getColumnName());
            if (i < columns.length - 1) {
                sb.append(TOKEN_COMMA_SPACE);
            }
            i++;
        }
        sb.append(')');
        return sb.toString();
    }

    protected abstract String getTokenIdDefinition();

}