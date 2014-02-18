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

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.api.registry.model.IndexType;
import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.IIndexElement;
import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.config.translate.sql.exception.SqlProcessingException;
import net.firejack.platform.core.config.translate.sql.token.ForeignKeyProcessor;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.upgrader.dbengine.DialectType;

import java.util.List;
import java.util.Set;


class MySql5Dialect extends BaseSqlDialect {

    private static final String TOKEN_SPACE = " ";
    //private static final String TOKEN_DOUBLE_SPACE = "  ";
    private static final String TOKEN_COMMA_SPACE = ", ";
    private static final String TOKEN_COMMA_EOL = ",\n";

    private static final String TOKEN_ID_DEFINITION = " BIGINT NOT NULL AUTO_INCREMENT,\n";

    private static final String TOKEN_ALTER_COLUMN = "ALTER COLUMN ";
    private static final String TOKEN_CHANGE_COLUMN = "CHANGE COLUMN ";

    private static final String TOKEN_ADD_PRIMARY_KEY = "ADD PRIMARY KEY ";
    private static final String TOKEN_ADD_UNIQUE_INDEX = "ADD UNIQUE INDEX ";
    private static final String TOKEN_DROP_INDEX = " DROP INDEX ";
    private static final String TOKEN_DROP_FK = " DROP FOREIGN KEY ";
    private static final String TOKEN_DROP_DEFAULT = "DROP DEFAULT";
    private static final String TOKEN_SET_DEFAULT = "SET DEFAULT ";
    //private static final String TOKEN_PRIMARY_KEY = "  PRIMARY KEY (";
    private static final String TOKEN_RESTRICT = "RESTRICT";

    private static final String TOKEN_ENGINE_INNODB = " ENGINE=InnoDB";
    private static final String TOKEN_CHARACTER_SET_UTF8 = "\nDEFAULT CHARACTER SET = utf8";
    private static final String TOKEN_COLLATE_UTF8 = "\nCOLLATE = utf8_general_ci";

    @Override
    public DialectType getType() {
        return DialectType.MySQL5;
    }

    @Override
    public String createTable(IEntityElement entity, boolean createIdField) {
        StringBuilder sb = new StringBuilder(super.createTable(entity, createIdField));
        sb.append(TOKEN_ENGINE_INNODB);
        sb.append(TOKEN_CHARACTER_SET_UTF8);
        sb.append(TOKEN_COLLATE_UTF8);
        return sb.toString();
    }

    @Override
    protected void constructTableBodyDefinition(
            StringBuilder sb, String idFieldName, IEntityElement entity,
            IFieldElement[] fieldsToProcess) {
        String tableName = getSqlNamesResolver().resolveTableName(entity);
        sb.append(TOKEN_DOUBLE_SPACE)
                .append(wrapWithSqlQuote(idFieldName))
                .append(TOKEN_ID_DEFINITION);
        addFieldDefinitions(tableName, sb, true, fieldsToProcess);
        sb.append(" KEY `PRIMARY_KEY` ").append(TOKEN_OPEN_BRACE).append(idFieldName).append(TOKEN_CLOSE_BRACE);
    }

    @Override
    protected String getTokenIdDefinition() {
        return TOKEN_ID_DEFINITION;
    }

    @Override
    public String createStagingTable(IEntityElement entity, String refField1, String refField2) throws SqlProcessingException {
        String createTableBaseSql = super.createStagingTable(entity, refField1, refField2);
        StringBuilder sb = new StringBuilder(createTableBaseSql);
        sb.append(TOKEN_ENGINE_INNODB);
        sb.append(TOKEN_CHARACTER_SET_UTF8);
        sb.append(TOKEN_COLLATE_UTF8);
        return sb.toString();
    }

    @Override
    public String changeColumn(IEntityElement entity, IFieldElement oldField, IFieldElement newField) {
        StringBuilder sb = new StringBuilder();
        String tableName = wrapWithSqlQuote(getTableName(entity));
        sb.append(TOKEN_ALTER_TABLE).append(tableName).append(TOKEN_SPACE);
        if (objEquals(oldField.getType(), newField.getType()) &&
                oldField.isRequired() == newField.isRequired() &&
                !objEquals(oldField.getDefaultValue(), newField.getDefaultValue())) {
            Object defaultValue = newField.getDefaultValue();
            sb.append(TOKEN_ALTER_COLUMN).append(oldField.getName()).append(TOKEN_SPACE);
            if (defaultValue == null) {
                sb.append(TOKEN_DROP_DEFAULT);
            } else {
                sb.append(TOKEN_SET_DEFAULT);
                if (oldField.getType().isString()) {
                    sb.append('\'').append(defaultValue).append('\'');
                } else {
                    sb.append(defaultValue);
                }
            }
        } else {
            sb.append(TOKEN_CHANGE_COLUMN).append(oldField.getName()).append(TOKEN_SPACE);
            sb.append(populateColumnDefinition(tableName, newField));
        }
        return sb.toString();
    }

    @Override
    public ForeignKeyProcessor getAddFkToken(String keyName, String fkTable, String fkFieldName, String pkTable, String pkFieldName) {
        ForeignKeyProcessor fkProcessor = new ForeignKeyProcessor();
        fkProcessor.setSqlDialect(this);
        fkProcessor.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(fkTable))//.append(TOKEN_SPACE)
//                .append(TOKEN_ADD_INDEX).append(wrapWithSqlQuote(keyName)).space()
//                .inBraces(wrapWithSqlQuote(fkFieldName)).append(TOKEN_COMMA_EOL)
                .append(TOKEN_ADD_CONSTRAINT).append(wrapWithSqlQuote(keyName))
                .append(TOKEN_FOREIGN_KEY).inBraces(wrapWithSqlQuote(fkFieldName))
                .append(TOKEN_REFERENCES).append(wrapWithSqlQuote(pkTable))
                .append(TOKEN_OPEN_BRACE).append(StringUtils.defaultIfEmpty(pkFieldName, TOKEN_ID)).append(TOKEN_CLOSE_BRACE);
        return fkProcessor;
    }

    public ForeignKeyProcessor addOnUpdateSection(ForeignKeyProcessor fkProcessor, RelationshipOption option) {
        if (option != RelationshipOption.SET_DEFAULT) {
            fkProcessor.append(TOKEN_ON_UPDATE);
            fkProcessor = processReferenceOption(option, fkProcessor);
        }
        return fkProcessor;
    }

    public ForeignKeyProcessor addOnDeleteSection(ForeignKeyProcessor fkProcessor, RelationshipOption option) {
        if (option != RelationshipOption.SET_DEFAULT) {
            fkProcessor.append(TOKEN_ON_DELETE);
            fkProcessor = processReferenceOption(option, fkProcessor);
        }
        return fkProcessor;
    }

    @Override
    public String addUniqueKey(String keyName, String tableWithUniqueKeys, Set<String> ukParticipants) {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(tableWithUniqueKeys)).append(TOKEN_SPACE)
                .append(TOKEN_ADD_UNIQUE_INDEX).append(wrapWithSqlQuote(keyName)).append(TOKEN_OPEN_BRACE);
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
                .append(TOKEN_DROP_FK).append(keyName);
        return sb.toString();
    }

    @Override
    public String addIndex(IEntityElement rootEntity, IIndexElement diffTarget) {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(getTableName(rootEntity))).append(TOKEN_SPACE);
        if (IndexType.PRIMARY.equals(diffTarget.getType())) {
            sb.append(TOKEN_ADD_PRIMARY_KEY).append(TOKEN_OPEN_BRACE);
        } else if (IndexType.UNIQUE.equals(diffTarget.getType())) {
            sb.append(TOKEN_ADD_UNIQUE_INDEX).append(wrapWithSqlQuote(diffTarget.getName())).append(TOKEN_OPEN_BRACE);
        } else {
            sb.append(TOKEN_ADD_INDEX).append(wrapWithSqlQuote(diffTarget.getName())).append(TOKEN_OPEN_BRACE);
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
        sb.append(TOKEN_ALTER_TABLE).append(tableName)
                .append(TOKEN_DROP_INDEX).append(keyName);
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
            case LONG_TEXT: typeRepresentation = "MEDIUMTEXT"; break;
            case UNLIMITED_TEXT: typeRepresentation = "LONGTEXT"; break;
            case RICH_TEXT: typeRepresentation = "LONGTEXT"; break;
            case URL: typeRepresentation = "VARCHAR(2048)"; break;
            case IMAGE_FILE: typeRepresentation = "VARCHAR(1024)"; break;
            case DATE: typeRepresentation = "DATE"; break;
            case TIME: typeRepresentation = "TIME"; break;
            case EVENT_TIME: typeRepresentation = "DATETIME"; break;
            case CREATION_TIME: typeRepresentation = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"; break;
            case UPDATE_TIME: typeRepresentation = "TIMESTAMP"; break;
            case INTEGER_NUMBER: typeRepresentation = "INTEGER"; break;
            case LARGE_NUMBER: typeRepresentation = "BIGINT"; break;
            case DECIMAL_NUMBER: typeRepresentation = "DOUBLE"; break;
            case CURRENCY: typeRepresentation = "DECIMAL(19,4)"; break;
            case PHONE_NUMBER: typeRepresentation = "VARCHAR(16)"; break;
            case SSN: typeRepresentation = "VARCHAR(16)"; break;
            case FLAG: typeRepresentation = "BIT"; break;
            case BLOB: typeRepresentation = "BLOB"; break;
            case OBJECT: typeRepresentation = null; break;
            case MAP: typeRepresentation = null; break;
            case LIST: typeRepresentation = null; break;
            default: typeRepresentation = null;
        }

        return typeRepresentation;
    }

    private ForeignKeyProcessor processReferenceOption(RelationshipOption referenceOption, ForeignKeyProcessor sb) {
        switch (referenceOption) {
            case CASCADE: sb.append(TOKEN_CASCADE); break;
            case NO_ACTION: sb.append(TOKEN_NO_ACTION); break;
            case SET_NULL: sb.append(TOKEN_SET_NULL); break;
            case RESTRICT: sb.append(TOKEN_RESTRICT);
        }
        return sb;
    }

}