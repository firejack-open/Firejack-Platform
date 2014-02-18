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

package net.firejack.platform.core.config.meta.parse;

import net.firejack.platform.api.registry.model.FieldType;
import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.core.config.meta.IRelationshipElement;
import net.firejack.platform.core.config.meta.construct.*;
import net.firejack.platform.core.config.meta.context.IUpgradeConfigContext;
import net.firejack.platform.core.config.meta.exception.ConfigSourceException;
import net.firejack.platform.core.config.meta.exception.ParseException;
import net.firejack.platform.core.config.meta.utils.DiffUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class XmlFromExistantDbDescriptorParser implements IPackageDescriptorParser<DataSource> {

    private static final Logger logger = Logger.getLogger(XmlFromExistantDbDescriptorParser.class);

    private static final String[] TABLES_RS = new String[] {
            "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE", "REMARKS", "TYPE_CAT",
            "TYPE_SCHEM", "TYPE_NAME", "SELF_REFERENCING_COL_NAME", "REF_GENERATION"
    };

    private static final String[] COLUMNS_RS = new String[] {
            "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME",
            "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "NUM_PREC_RADIX", "NULLABLE", "REMARKS",
            "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION",
            "IS_NULLABLE", "SCOPE_CATLOG", "SCOPE_SCHEMA", "SCOPE_TABLE", "SOURCE_DATA_TYPE", "IS_AUTOINCREMENT",
    };

    private static final String[] FK_RS = new String[] {
            "PKTABLE_CAT", "PKTABLE_SCHEM", "PKTABLE_NAME", "PKCOLUMN_NAME",
            "FKTABLE_CAT", "FKTABLE_SCHEM", "FKTABLE_NAME", "FKCOLUMN_NAME",
            "KEY_SEQ", "UPDATE_RULE", "DELETE_RULE", "FK_NAME", "PK_NAME",
            "DEFERRABILITY"
    };

    private static final String[] U_RS = new String[] {
            "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "NON_UNIQUE", "INDEX_QUALIFIER",
            "INDEX_NAME", "TYPE", "ORDINAL_POSITION", "COLUMN_NAME", "ASC_OR_DESC", "CARDINALITY",
            "PAGES", "FILTER_CONDITION"
    };

    @Override
    public IPackageDescriptor parsePackageDescriptor(IUpgradeConfigContext<DataSource> context) throws ParseException {
        DataSource ds;
        try {
            ds = context.getConfigSource();
        } catch (ConfigSourceException e) {
            logger.error(e.getMessage(), e);
            throw new ParseException();
        }
        DatabaseMetaData dbMeta;
        try {
            Connection connection = ds.getConnection();
            dbMeta = connection.getMetaData();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ParseException();
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            throw new ParseException();
        }

        try {
            ResultSet tablesRS = dbMeta.getTables(null, null, null, null);
            Tuple<List<IEntityElement>, String> domainObjectsTuple = getDomainObjects(tablesRS, dbMeta);
            List<IEntityElement> entityList = new ArrayList<IEntityElement>();
            List<IRelationshipElement> relationshipElements = new ArrayList<IRelationshipElement>();
            for (IEntityElement entity : domainObjectsTuple.getKey()) {
                ResultSet tableColumnsRS = dbMeta.getColumns(null, null, entity.getName(), null);
                wrapFields(entity, tableColumnsRS);

                ResultSet fkRS = dbMeta.getImportedKeys(null, null, entity.getName());
                relationshipElements.addAll(wrapFKConstraints(domainObjectsTuple.getValue(), domainObjectsTuple.getKey(), entity, fkRS));
            }
            PackageDescriptor versionPackage = new PackageDescriptor(domainObjectsTuple.getValue(), "1.0");
            versionPackage.setConfiguredEntities(entityList);
            versionPackage.setRelationships(relationshipElements);
            return versionPackage;
        } catch (SQLException e) {
            logger.error("Failed to retrieve database meta data.");
            throw new ParseException(e.getMessage(), e);
        }
    }

    private Tuple<List<IEntityElement>, String> getDomainObjects(ResultSet tableRS, DatabaseMetaData dbMeta) throws SQLException, ParseException {
        List<IEntityElement> entity = new ArrayList<IEntityElement>();
        String dbName = null;
        while (tableRS.next()) {
            if (StringUtils.isBlank(dbName)) {
                dbName = tableRS.getString(TABLES_RS[1]);
            }
            String tableType = tableRS.getString(TABLES_RS[3]);
            TableType type = TableType.fromRsValue(tableType);
            if (type == TableType.TABLE) {
                String tableName = tableRS.getString(TABLES_RS[2]);
                ResultSet uniqueIndexRS = dbMeta.getIndexInfo(null, null, tableName, true, false);
                IEntityElement packageEntity = wrapWithUniqueIndexes(tableName, uniqueIndexRS);
                ConfigElementFactory factory = ConfigElementFactory.getInstance();
                factory.attachPath(packageEntity, dbName);
                entity.add(packageEntity);
            }
        }
        return new Tuple<List<IEntityElement>, String>(entity, dbName);
    }

    private List<IFieldElement> wrapFields(IEntityElement entity, ResultSet tableColumnsRS)
            throws SQLException, ParseException {
        List<IFieldElement> fields = new ArrayList<IFieldElement>();
        ConfigElementFactory factory = ConfigElementFactory.getInstance();
        while (tableColumnsRS.next()) {
            String columnName = tableColumnsRS.getString(COLUMNS_RS[3]);
            if (columnName.equalsIgnoreCase("id")) {
                continue;
            }
            Integer columnSize = tableColumnsRS.getInt(COLUMNS_RS[6]);
            Integer decimalDigits = tableColumnsRS.getObject(COLUMNS_RS[8]) != null ?
                    tableColumnsRS.getInt(COLUMNS_RS[8]) : null;
            int jdbcType = tableColumnsRS.getInt(COLUMNS_RS[4]);
            FieldType fieldType = DiffUtils.getFieldType(jdbcType, columnSize, decimalDigits);
            if (fieldType == null) {
                logger.warn("Couldn't process jdbcType[" + jdbcType + "]...");
                throw new ParseException("The JDBC type [" + jdbcType + "]");
            }
            Boolean required = getYesNoStatus(tableColumnsRS.getString(COLUMNS_RS[17]));
            if (required == null) {
                logger.warn("Nullability status is not unknown. Use null as default value for column [" + columnName + "]");
                required = Boolean.TRUE;
            } else {
                required = !required;
            }

            String defaultValue = tableColumnsRS.getString(COLUMNS_RS[12]);

            IFieldElement field = factory.produceField(
                    entity, columnName, fieldType, required, defaultValue);
            fields.add(field);
        }
        return fields;
    }

    private List<IRelationshipElement> wrapFKConstraints(String dbName, List<IEntityElement> entityList, IEntityElement entity, ResultSet fkRS) throws SQLException {
        List<IRelationshipElement> fkList = new ArrayList<IRelationshipElement>();
        ConfigElementFactory factory = ConfigElementFactory.getInstance();
        IFieldElement[] fields = entity.getFields();
        while (fkRS.next()) {
            String fkName = fkRS.getString(FK_RS[11]);
            String pkTable = fkRS.getString(FK_RS[2]);
            String fkColumn = fkRS.getString(FK_RS[7]);
            IFieldElement referenceField = null;
            for (IFieldElement field : fields) {
                if (field.getName().equalsIgnoreCase(fkColumn)) {
                    referenceField = field;
                    break;
                }
            }
            IEntityElement referencedEntity = null;
            for (IEntityElement obj : entityList) {
                if (obj.getName().equalsIgnoreCase(pkTable)) {
                    referencedEntity = obj;
                    break;
                }
            }
            if (referenceField == null) {
                throw new IllegalStateException("Failed to find reference field.");
            }
            if (referencedEntity == null || StringUtils.isBlank(referencedEntity.getName())) {
                throw new IllegalStateException("Failed to find referenced entity.");
            }
            Reference sourceRef = new Reference();
            sourceRef.setRefPath(dbName + "." + pkTable);

            Reference targetRef = new Reference();
            targetRef.setRefPath(dbName + "." + referencedEntity.getName());

            IRelationshipElement relationshipElement = factory.produceTypeRelationship(fkName, "", sourceRef, targetRef,
                    getReferenceOption(fkRS.getInt(FK_RS[10])),
                    getReferenceOption(fkRS.getInt(FK_RS[9])));
            factory.attachPath(relationshipElement, dbName);
            fkList.add(relationshipElement);
        }
        return fkList;
    }

    private RelationshipOption getReferenceOption(int deleteRule) {
        switch (deleteRule) {
            case DatabaseMetaData.importedKeyNoAction: return RelationshipOption.NO_ACTION;
            case DatabaseMetaData.importedKeyCascade: return RelationshipOption.CASCADE;
            case DatabaseMetaData.importedKeyRestrict: return RelationshipOption.RESTRICT;
            case DatabaseMetaData.importedKeySetNull: return RelationshipOption.SET_NULL;
            default: return null;
        }
    }

    private IEntityElement wrapWithUniqueIndexes(String entityName, ResultSet uniqueIndexRS)
            throws SQLException, ParseException {
        List<CompoundKeyColumnsRule> compoundKeyColumnsRuleList = new ArrayList<CompoundKeyColumnsRule>();
        String lastUniqueKeyIndexName = "";
        Set<CompoundKeyParticipantColumn> compoundKeyParticipantColumns = new HashSet<CompoundKeyParticipantColumn>();
        while (uniqueIndexRS.next()) {
            String indexName = uniqueIndexRS.getString(U_RS[5]);
            if (!"PRIMARY".equalsIgnoreCase(indexName)) {
                if (!lastUniqueKeyIndexName.equals(indexName)) {
                    if (!compoundKeyParticipantColumns.isEmpty()) {
                        CompoundKeyColumnsRule compoundKeyColumnsRule = new CompoundKeyColumnsRule(indexName);
                        compoundKeyColumnsRule.setCompoundKeyParticipantColumns(new HashSet<CompoundKeyParticipantColumn>(compoundKeyParticipantColumns));
                        compoundKeyColumnsRuleList.add(compoundKeyColumnsRule);
                    }
                    lastUniqueKeyIndexName = indexName;
                    compoundKeyParticipantColumns.clear();
                }
                CompoundKeyParticipantColumn column = new CompoundKeyParticipantColumn();
                column.setColumnName(uniqueIndexRS.getString(U_RS[8]));
                compoundKeyParticipantColumns.add(column);
            }
        }
        if (!compoundKeyParticipantColumns.isEmpty()) {
            CompoundKeyColumnsRule compoundKeyColumnsRule = new CompoundKeyColumnsRule(lastUniqueKeyIndexName);
            compoundKeyColumnsRule.setCompoundKeyParticipantColumns(new HashSet<CompoundKeyParticipantColumn>(compoundKeyParticipantColumns));
            compoundKeyColumnsRuleList.add(compoundKeyColumnsRule);
        }
        ConfigElementFactory factory = ConfigElementFactory.getInstance();
        return compoundKeyColumnsRuleList.isEmpty() ? factory.producePackageEntity(entityName) :
                factory.producePackageEntity(entityName, compoundKeyColumnsRuleList);
    }

    private Boolean getYesNoStatus(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        value = value.trim();
        return "YES".equalsIgnoreCase(value) ? Boolean.TRUE : "NO".equalsIgnoreCase(value) ? Boolean.FALSE : null;
    }

    @SuppressWarnings("unused")
    private enum TableType {
        TABLE, VIEW, SYSTEM_TABLE("SYSTEM TABLE"), GLOBAL_TEMPORARY("GLOBAL TEMPORARY"),
        LOCAL_TEMPORARY("LOCAL TEMPORARY"), ALIAS, SYNONYM;

        private String rsValue;

        private TableType(String rsValue) {
            this.rsValue = rsValue;
        }

        private TableType() {
            this.rsValue = name();
        }

        public String getRsValue() {
            return rsValue;
        }

        public static TableType fromRsValue(String rsValue) {
            TableType[] types = values();
            for (TableType type : types) {
                if (type.getRsValue().equalsIgnoreCase(rsValue)) {
                    return type;
                }
            }
            return null;
        }
    }

}