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

package net.firejack.platform.core.config.translate.sql;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.api.registry.model.IndexType;
import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.IIndexElement;
import net.firejack.platform.core.config.meta.construct.CompoundKeyColumnsRule;
import net.firejack.platform.core.config.meta.construct.CompoundKeyParticipantColumn;
import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.config.translate.sql.exception.SqlProcessingException;
import net.firejack.platform.core.config.translate.sql.token.ForeignKeyProcessor;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.upgrader.dbengine.DialectType;

import java.util.List;
import java.util.Set;

class MSSqlDialect extends BaseSqlDialect {

    protected static final String TOKEN_ADD_CONSTRAINT = " ADD CONSTRAINT ";
    protected static final String TOKEN_DROP_CONSTRAINT = "DROP CONSTRAINT ";
    protected static final String TOKEN_DROP_INDEX = "DROP INDEX ";
    protected static final String TOKEN_DEFAULT = "DEFAULT ";
    protected static final String TOKEN_RENAME_TABLE = "EXEC sp_rename ";
    protected static final String TOKEN_DROP_TABLE = "DROP TABLE ";
    protected static final String TOKEN_FOR = "FOR ";
    //private static final String TOKEN_ID_DEFINITION = " BIGINT PRIMARY KEY IDENTITY,\n";
    private static final String TOKEN_ID_DEFINITION = " BIGINT IDENTITY,\n";
    private static final String TOKEN_SET_DEFAULT = "SET DEFAULT";
    private static final String TOKEN_UNIQUE = " UNIQUE (";
    private static final String TOKEN_CONSTRAINT = "CONSTRAINT ";
    private static final String TOKEN_ALTER_COLUMN = "ALTER COLUMN ";
    private static final String TOKEN_ADD = " ADD ";

    @Override
    public DialectType getType() {
        return DialectType.MSSQL;
    }

    @Override
    public String changeColumn(IEntityElement entity, IFieldElement oldField, IFieldElement newField) throws SqlProcessingException {
        StringBuilder sb = new StringBuilder();
        String tableName = getTableName(entity);
        String fieldName = getSqlNamesResolver().resolveColumnName(newField);

        sb.append(dropDefaultConstraintIfExist(tableName, fieldName));
        sb.append(DialectType.MSSQL.getSeparator());
        sb.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(tableName)).append(TOKEN_SPACE)
                .append(TOKEN_ALTER_COLUMN).append(wrapWithSqlQuote(fieldName)).append(TOKEN_SPACE)
                .append(getColumnType(newField.getType())).append(TOKEN_SPACE)
                .append(newField.isRequired() ? TOKEN_NOT_NULL_SPACE : TOKEN_NULL_SPACE);
        sb.append(addDefaultConstraint(newField, tableName));

        return sb.toString();
    }

    protected String populateColumnDefinition(String tableName, IFieldElement field) {
        StringBuilder sb = new StringBuilder();
        String fieldName = getSqlNamesResolver().resolveColumnName(field);
        String columnType = getColumnType(field.getType());
        sb.append(wrapWithSqlQuote(fieldName)).append(TOKEN_SPACE)
                .append(columnType).append(TOKEN_SPACE)
                .append(field.isRequired() ? TOKEN_NOT_NULL_SPACE : TOKEN_NULL_SPACE);
        if (!columnType.toUpperCase().contains(" DEFAULT ") && field.getDefaultValue() != null && field.getType() != null) {
            sb.append(" CONSTRAINT ").append(
                    wrapWithSqlQuote(defaultValueConstraintName(tableName, field.getName())))
                    .append(TOKEN_SPACE).append(TOKEN_DEFAULT_SPACE);
            if (field.getType().isBoolean()) {
                sb.append(String.valueOf(field.getDefaultValue()).equalsIgnoreCase("true") ? 1 : 0);
            } else if (field.getType().isString()) {
                sb.append('\'').append(field.getDefaultValue()).append('\'');
            } else {
                sb.append(field.getDefaultValue());
            }
            sb.append(TOKEN_SPACE);
        }
        return sb.toString();
    }

    @Override
    public ForeignKeyProcessor getAddFkToken(String keyName, String fkTable, String fkFieldName, String pkTable, String pkFieldName) {
       ForeignKeyProcessor fkProcessor = new ForeignKeyProcessor();
       fkProcessor.setSqlDialect(this);
       fkProcessor.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(fkTable))
               .append(TOKEN_ADD_CONSTRAINT).append(wrapWithSqlQuote(keyName))
               .append(TOKEN_FOREIGN_KEY).inBraces(wrapWithSqlQuote(fkFieldName))
               .append(TOKEN_REFERENCES).append(wrapWithSqlQuote(pkTable))
               .append(TOKEN_OPEN_BRACE).append(StringUtils.defaultIfEmpty(pkFieldName, TOKEN_ID)).append(TOKEN_CLOSE_BRACE);
       return fkProcessor;
    }

    @Override
    public String addUniqueKey(String keyName, String tableWithUniqueKeys, Set<String> ukParticipants) {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(tableWithUniqueKeys))
                .append(TOKEN_ADD_CONSTRAINT).append(wrapWithSqlQuote(keyName)).append(TOKEN_UNIQUE);
        int i = 0;
        for (String column : ukParticipants) {
            sb.append(wrapWithSqlQuote(column));
            if (++i != ukParticipants.size()) {
                sb.append(TOKEN_COMMA_SPACE);
            }
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public String dropForeignKey(String tableName, String keyName) throws SqlProcessingException {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_ALTER_TABLE).append(tableName)
                .append(TOKEN_DROP_CONSTRAINT).append(keyName);
        return sb.toString();
    }

    @Override
    public String addIndex(IEntityElement rootEntity, IIndexElement diffTarget) {
        StringBuilder sb = new StringBuilder();
        String tableName = getTableName(rootEntity);
        sb.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(tableName));
        if (IndexType.PRIMARY.equals(diffTarget.getType())) {
            sb.append(TOKEN_ADD_CONSTRAINT).append(wrapWithSqlQuote(
                    tableName + "_" + diffTarget.getName())).append(TOKEN_PRIMARY_KEY);
        } else if (IndexType.UNIQUE.equals(diffTarget.getType())) {
            sb.append(TOKEN_ADD_CONSTRAINT).append(wrapWithSqlQuote(diffTarget.getName())).append(TOKEN_UNIQUE);
        } else {
            sb.append(TOKEN_SPACE).append(TOKEN_ADD_INDEX).append(wrapWithSqlQuote(diffTarget.getName())).append(TOKEN_OPEN_BRACE);
        }
        List<IFieldElement> fields = diffTarget.getFields();
        List<Reference> references = diffTarget.getEntities();
        int columns = 0;
        int i = 0;
        if (references != null) {
            columns += references.size();
        }
        if (fields != null) {
            columns += fields.size();
            for (IFieldElement field : fields) {
                String fieldName = getSqlNamesResolver().resolveColumnName(field);
                sb.append(wrapWithSqlQuote(fieldName));
                if (++i != columns) {
                    sb.append(TOKEN_COMMA_SPACE);
                }
            }
        }
        if (references != null) {
            for (Reference reference : references) {
                String fieldName = getSqlNamesResolver().resolveColumnName(reference.getConstraintName());
                sb.append(wrapWithSqlQuote(fieldName));
                if (++i != columns) {
                    sb.append(TOKEN_COMMA_SPACE);
                }
            }
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public String dropIndex(String tableName, String keyName) throws SqlProcessingException {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_DROP_INDEX).append(keyName)
                .append(" ON ").append(tableName);
        return sb.toString();
    }

    @Override
    public String addColumn(IEntityElement entity, IFieldElement field) {
        String tableName = getTableName(entity);
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(tableName))
                .append(TOKEN_ADD).append(populateColumnDefinition(tableName, field));
        return sb.toString();
    }

    @Override
    public String modifyTableName(String oldTableName, String newTableName) throws SqlProcessingException {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_RENAME_TABLE).append(oldTableName)
                .append(", ").append(newTableName).append(getType().getSeparator());
        return sb.toString();
    }

    @Override
    public String dropTable(String entityName) throws SqlProcessingException {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_DROP_TABLE).append(entityName).append(';');
        return sb.toString();
    }

    @Override
    public String getColumnType(FieldType fieldType) {
        if (fieldType == null) {
            throw new IllegalArgumentException(MSG_FIELD_TYPE_PARAMETER_IS_NULL);
        }

        String typeRepresentation;
        switch (fieldType) {
            case UNIQUE_ID: typeRepresentation = "VARCHAR(255)"; break;
            case NUMERIC_ID: typeRepresentation = "BIGINT"; break;
            case CODE: typeRepresentation = "VARCHAR(16)"; break;
            case LABEL: typeRepresentation = "VARCHAR(128)"; break;
            case LOOKUP: typeRepresentation = "VARCHAR(1024)"; break;
            case NAME: typeRepresentation = "VARCHAR(64)"; break;
            case DESCRIPTION: typeRepresentation = "VARCHAR(4096)"; break;
            case PASSWORD: typeRepresentation = "VARCHAR(32)"; break;
            case SECRET_KEY: typeRepresentation = "VARCHAR(1024)"; break;
            case TINY_TEXT: typeRepresentation = "VARCHAR(64)"; break;
            case SHORT_TEXT: typeRepresentation = "VARCHAR(255)"; break;
            case MEDIUM_TEXT: typeRepresentation = "VARCHAR(1024)"; break;
            case LONG_TEXT: typeRepresentation = "TEXT"; break;
            case UNLIMITED_TEXT: typeRepresentation = "TEXT"; break;
            case RICH_TEXT: typeRepresentation = "TEXT"; break;
            case URL: typeRepresentation = "VARCHAR(2048)"; break;
            case IMAGE_FILE: typeRepresentation = "VARCHAR(1024)"; break;
            case DATE: typeRepresentation = "SMALLDATETIME"; break;
            case TIME: typeRepresentation = "SMALLDATETIME"; break;
            case EVENT_TIME: typeRepresentation = "DATETIME"; break;
            case CREATION_TIME: typeRepresentation = "DATETIME DEFAULT CURRENT_TIMESTAMP"; break;
            case UPDATE_TIME: typeRepresentation = "DATETIME"; break;
            case INTEGER_NUMBER: typeRepresentation = "INT"; break;
            case LARGE_NUMBER: typeRepresentation = "BIGINT"; break;
            case DECIMAL_NUMBER: typeRepresentation = "FLOAT(25)"; break;
            case CURRENCY: typeRepresentation = "DECIMAL(19,4)"; break;
            case PHONE_NUMBER: typeRepresentation = "VARCHAR(16)"; break;
            case SSN: typeRepresentation = "VARCHAR(16)"; break;
            case FLAG: typeRepresentation = "BIT"; break;
            case BLOB: typeRepresentation = "VARBINARY"; break;
            case OBJECT: typeRepresentation = null; break;
            case MAP: typeRepresentation = null; break;
            case LIST: typeRepresentation = null; break;
            default: typeRepresentation = null;
        }

        return typeRepresentation;
    }

    public ForeignKeyProcessor addOnUpdateSection(ForeignKeyProcessor fkProcessor, RelationshipOption option) {
        if (option != RelationshipOption.RESTRICT) {
            fkProcessor.append(TOKEN_ON_UPDATE);
            fkProcessor = processReferenceOption(option, fkProcessor);
        }
        return fkProcessor;
    }

    public ForeignKeyProcessor addOnDeleteSection(ForeignKeyProcessor fkProcessor, RelationshipOption option) {
        if (option != RelationshipOption.RESTRICT) {
            fkProcessor.append(TOKEN_ON_DELETE);
            fkProcessor = processReferenceOption(option, fkProcessor);
        }
        return fkProcessor;
    }

    private String defaultValueConstraintName(String tableName, String fieldName) {
        return tableName + "_" + fieldName + "_constr";
    }

    private String dropDefaultConstraintIfExist(String tableName, String fieldName) {
        StringBuilder sb = new StringBuilder();
        String defaultConstrName = defaultValueConstraintName(tableName, fieldName);
        // if condition
        sb.append(TOKEN_IF).append(TOKEN_OPEN_BRACE).append("OBJECT_ID")
                .append(TOKEN_OPEN_BRACE).append(wrapWithSqlQuote(defaultConstrName))
                .append(TOKEN_COMMA_SPACE).append("'D'").append(TOKEN_CLOSE_BRACE)
                .append(TOKEN_IS_NOT_NULL).append(TOKEN_CLOSE_BRACE).append("\n");
        // drop constraint
        sb.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(tableName)).append(TOKEN_SPACE)
                .append(TOKEN_DROP_CONSTRAINT).append(TOKEN_SPACE)
                .append(defaultConstrName).append(TOKEN_SEMICOLON_EOL);
        return sb.toString();
    }

    private String addDefaultConstraint(IFieldElement field, String tableName) {
        StringBuilder sb = new StringBuilder();
        String fieldName = getSqlNamesResolver().resolveColumnName(field);
        String defaultConstrName = defaultValueConstraintName(tableName, fieldName);
        Object defaultValue = field.getDefaultValue();
        if (defaultValue != null && field.getType() != null) {
            sb.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(tableName))
                    .append(TOKEN_ADD_CONSTRAINT).append(TOKEN_SPACE)
                    .append(defaultConstrName).append(TOKEN_SPACE).append(TOKEN_DEFAULT);
            if (field.getType().isBoolean()) {
                sb.append(String.valueOf(field.getDefaultValue()).equalsIgnoreCase("true") ? 1 : 0);
            } else if (field.getType().isString()) {
                sb.append('\'').append(defaultValue).append('\'');
            } else {
                sb.append(defaultValue);
            }
            sb.append(TOKEN_SPACE).append(TOKEN_FOR).append(fieldName);
        }
        return sb.toString();
    }

    @Override
    protected String wrapWithSqlQuote(String name) {
        return '[' + name + ']';
    }

    /*@Override
    protected void constructTableBodyDefinition(
            StringBuilder sb, String idFieldName, IEntityElement entity,
            IFieldElement[] fieldsToProcess) {
        sb.append(TOKEN_DOUBLE_SPACE)
                .append(wrapWithSqlQuote(idFieldName))
                .append(TOKEN_ID_DEFINITION);
        addFieldDefinitions(sb, false, fieldsToProcess);
    }*/

    @Override
    protected String getTokenIdDefinition() {
        return TOKEN_ID_DEFINITION;
    }

    @Override
    protected String uniqueEntry(CompoundKeyColumnsRule compoundKeyColumnsRule) {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_CONSTRAINT).append(compoundKeyColumnsRule.getName()).append(TOKEN_UNIQUE);
        int i = 0;
        CompoundKeyParticipantColumn[] columns =
                compoundKeyColumnsRule.getCompoundKeyParticipantColumns();
        for (CompoundKeyParticipantColumn column : columns) {
            sb.append(column.getColumnName());
            if (i++ < columns.length - 1) {
                sb.append(TOKEN_COMMA_SPACE);
            }
        }
        sb.append(')');
        return sb.toString();
    }

    private ForeignKeyProcessor processReferenceOption(RelationshipOption referenceOption, ForeignKeyProcessor sb) {
        switch (referenceOption) {
            case CASCADE: sb.append(TOKEN_CASCADE); break;
            case NO_ACTION: sb.append(TOKEN_NO_ACTION); break;
            case SET_NULL: sb.append(TOKEN_SET_NULL); break;
            case SET_DEFAULT: sb.append(TOKEN_SET_DEFAULT);
        }
        return sb;
    }

}