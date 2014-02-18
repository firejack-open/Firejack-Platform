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
import net.firejack.platform.core.config.meta.construct.CompoundKeyColumnsRule;
import net.firejack.platform.core.config.meta.construct.CompoundKeyParticipantColumn;
import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.config.translate.SqlTranslationResult;
import net.firejack.platform.core.config.translate.sql.exception.SqlProcessingException;
import net.firejack.platform.core.config.translate.sql.token.ForeignKeyProcessor;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.model.upgrader.dbengine.DialectType;

import java.util.List;
import java.util.Set;

public class OracleDialect extends BaseSqlDialect {

    private static final String TOKEN_ADD_CONSTRAINT = " ADD CONSTRAINT ";
    private static final String TOKEN_UNIQUE = " UNIQUE (";
    private static final String TOKEN_CONSTRAINT = " CONSTRAINT ";
    private static final String TOKEN_PRIMARY_KEY = " PRIMARY KEY (";
    private static final String TOKEN_ID_TYPE_DEFINITION = " NUMBER(19,0) NOT NULL,\n";
    //private static final String TOKEN_PK_SUFFIX = "_pk";
    private static final String TOKEN_ADD = " ADD (";
    private static final String TOKEN_MODIFY = "MODIFY ( ";
    private static final String TOKEN_DROP_CONSTRAINT = " DROP CONSTRAINT ";

    /*private static final String SQL_HIBERNATE_SEQUENCE =
            "create sequence hibernate_sequence start with 1 increment by 1 nomaxvalue";*/

    private static final String SQL_HIBERNATE_SEQUENCE =
            "declare\n" +
            "    c number;\n" +
            "    begin\n" +
            "        select count(*) into c from user_sequences where sequence_name = 'HIBERNATE_SEQUENCE';\n" +
            "        if c = 0  then\n" +
            "            EXECUTE IMMEDIATE 'CREATE SEQUENCE HIBERNATE_SEQUENCE  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE';\n" +
            "        end if;\n" +
            "    end;";

    @Override
    public DialectType getType() {
        return DialectType.ORACLE;
    }

    @Override
    public void initializeScript(SqlTranslationResult resultState) {
        resultState.addInitialStatement(SQL_HIBERNATE_SEQUENCE);
    }

    @Override
    public String changeColumn(
            IEntityElement entity, IFieldElement oldField, IFieldElement newField)
            throws SqlProcessingException {
        StringBuilder sb = new StringBuilder();
        String tableName = wrapWithSqlQuote(getTableName(entity));
        sb.append(TOKEN_ALTER_TABLE).append(tableName).append(TOKEN_SPACE)
                .append(TOKEN_MODIFY).append(populateColumnDefinition(tableName, newField))
                .append(TOKEN_SPACE).append(')');
        return sb.toString();
    }

    @Override
    public ForeignKeyProcessor getAddFkToken(String keyName, String fkTable, String fkFieldName, String pkTable, String pkFieldName) {
        ForeignKeyProcessor fkProcessor = new ForeignKeyProcessor();
        fkProcessor.setSqlDialect(this);
        fkProcessor.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(fkTable)).append(TOKEN_SPACE)
                .append(TOKEN_ADD_CONSTRAINT).append(wrapWithSqlQuote(keyName))
                .append(TOKEN_FOREIGN_KEY).inBraces(wrapWithSqlQuote(fkFieldName))
                .append(TOKEN_REFERENCES).append(wrapWithSqlQuote(pkTable))
                .append(TOKEN_OPEN_BRACE).append(wrapWithSqlQuote(StringUtils.defaultIfEmpty(pkFieldName, TOKEN_ID))).append(TOKEN_CLOSE_BRACE);
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
    public String addIndex(IEntityElement rootEntity, IIndexElement diffTarget) {
        StringBuilder sb = new StringBuilder();
        String tableName = getTableName(rootEntity);
        if (diffTarget.getType() == IndexType.PRIMARY || diffTarget.getType() == IndexType.UNIQUE) {
            sb.append(TOKEN_ALTER_TABLE).append(wrapWithSqlQuote(tableName)).append(TOKEN_SPACE);
            if (IndexType.PRIMARY.equals(diffTarget.getType())) {
                sb.append(TOKEN_ADD_CONSTRAINT).append(wrapWithSqlQuote(tableName + "_" + diffTarget.getName())).append(TOKEN_PRIMARY_KEY);
            } else {
                sb.append(TOKEN_ADD_CONSTRAINT).append(wrapWithSqlQuote(diffTarget.getName())).append(TOKEN_UNIQUE);
            }
        } else {
            sb.append("CREATE INDEX ").append(wrapWithSqlQuote(diffTarget.getName())).append(" ON ")
                    .append(wrapWithSqlQuote(tableName)).append(TOKEN_OPEN_BRACE);
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
        sb.append(TOKEN_CLOSE_BRACE);
        return sb.toString();
    }

    @Override
    public String dropIndex(String tableName, String keyName) throws SqlProcessingException {
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_ALTER_TABLE).append(tableName)
                .append(TOKEN_DROP_CONSTRAINT).append(keyName);
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
    public ForeignKeyProcessor addOnUpdateSection(ForeignKeyProcessor fkProcessor, RelationshipOption option) {
        return fkProcessor;
    }

    @Override
    public ForeignKeyProcessor addOnDeleteSection(ForeignKeyProcessor fkProcessor, RelationshipOption option) {
        if (option == RelationshipOption.CASCADE || option == RelationshipOption.SET_NULL) {
            fkProcessor.append(TOKEN_ON_DELETE);
            fkProcessor.append(option == RelationshipOption.CASCADE ? TOKEN_CASCADE : TOKEN_SET_NULL);
        }
        return fkProcessor;
    }

    @Override
    public String addColumn(IEntityElement entity, IFieldElement field) {
        String tableName = wrapWithSqlQuote(getTableName(entity));
        StringBuilder sb = new StringBuilder();
        sb.append(TOKEN_ALTER_TABLE).append(tableName)
                .append(TOKEN_ADD).append(populateColumnDefinition(tableName, field))
                .append(')').append(TOKEN_SPACE);
        return sb.toString();
    }

    @Override
    public String getColumnType(FieldType fieldType) throws SqlProcessingException {
        if (fieldType == null) {
            throw new IllegalArgumentException(MSG_FIELD_TYPE_PARAMETER_IS_NULL);
        }

        String typeRepresentation;
        switch (fieldType) {
            case UNIQUE_ID: typeRepresentation = "VARCHAR2(255)"; break;
            case NUMERIC_ID: typeRepresentation = "NUMBER(19,0)"; break;
            case CODE: typeRepresentation = "VARCHAR2(16)"; break;
            case LABEL: typeRepresentation = "VARCHAR2(128)"; break;
            case LOOKUP: typeRepresentation = "VARCHAR2(1024)"; break;
            case NAME: typeRepresentation = "VARCHAR2(64)"; break;
            case DESCRIPTION: typeRepresentation = "VARCHAR2(4000)"; break;
            case PASSWORD: typeRepresentation = "VARCHAR2(32)"; break;
            case SECRET_KEY: typeRepresentation = "VARCHAR2(1024)"; break;
            case TINY_TEXT: typeRepresentation = "VARCHAR2(64)"; break;
            case SHORT_TEXT: typeRepresentation = "VARCHAR2(255)"; break;
            case MEDIUM_TEXT: typeRepresentation = "VARCHAR2(1024)"; break;
            case LONG_TEXT: typeRepresentation = "CLOB"; break;
            case UNLIMITED_TEXT: typeRepresentation = "CLOB"; break;
            case RICH_TEXT: typeRepresentation = "CLOB"; break;
            case URL: typeRepresentation = "VARCHAR2(2048)"; break;
            case IMAGE_FILE: typeRepresentation = "VARCHAR2(1024)"; break;
            case DATE: typeRepresentation = "DATE"; break;
            case TIME: typeRepresentation = "TIMESTAMP(0)"; break;
            case EVENT_TIME: typeRepresentation = "TIMESTAMP(0)"; break;
            case CREATION_TIME: typeRepresentation = "TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP"; break;
            case UPDATE_TIME: typeRepresentation = "TIMESTAMP(0)"; break;
            case INTEGER_NUMBER: typeRepresentation = "NUMBER(10, 0)"; break;
            case LARGE_NUMBER: typeRepresentation = "NUMBER(19, 0)"; break;
            case DECIMAL_NUMBER: typeRepresentation = "FLOAT(24)"; break;
            case CURRENCY: typeRepresentation = "FLOAT(24)"; break;
            case PHONE_NUMBER: typeRepresentation = "VARCHAR2(16)"; break;
            case SSN: typeRepresentation = "VARCHAR2(16)"; break;
            case FLAG: typeRepresentation = "NUMBER(1)"; break;
            case OBJECT: typeRepresentation = null; break;
            case MAP: typeRepresentation = null; break;
            case LIST: typeRepresentation = null; break;
            default: typeRepresentation = null;
        }

        return typeRepresentation;
    }

    /*@Override
    protected void constructTableBodyDefinition(
            StringBuilder sb, String idFieldName, IEntityElement entity,
            IFieldElement[] fieldsToProcess) {
        sb.append(TOKEN_DOUBLE_SPACE)
                .append(wrapWithSqlQuote(idFieldName))
                .append(TOKEN_ID_TYPE_DEFINITION);
        addFieldDefinitions(sb, true, fieldsToProcess);
        sb.append(TOKEN_CONSTRAINT).append(getTableName(entity)).append(TOKEN_PK_SUFFIX)
                .append(TOKEN_PRIMARY_KEY).append(idFieldName).append(')');
    }*/

    @Override
    protected String getTokenIdDefinition() {
        return TOKEN_ID_TYPE_DEFINITION;
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

    protected String populateColumnDefinition(String tableName, IFieldElement field) {
        StringBuilder sb = new StringBuilder();
        String fieldName = getSqlNamesResolver().resolveColumnName(field);
        sb.append(wrapWithSqlQuote(fieldName)).append(TOKEN_SPACE);
        String columnType = getColumnType(field.getType());
        sb.append(columnType).append(TOKEN_SPACE);
        if (!columnType.toUpperCase().contains(" DEFAULT ")) {
            renderDefaultValue(sb, field);
        }
        sb.append(field.isRequired() ? TOKEN_NOT_NULL_SPACE : TOKEN_NULL_SPACE);
        return sb.toString();
    }

    @Override
    protected String wrapWithSqlQuote(String name) {
        return StringUtils.wrapWith("\"", name.toUpperCase());
    }

}