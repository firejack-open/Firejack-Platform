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

import net.firejack.platform.core.config.meta.*;
import net.firejack.platform.core.config.meta.construct.Reference;
import net.firejack.platform.core.config.translate.sql.discriminator.DiscriminatorStrategyFactory;
import net.firejack.platform.core.config.translate.sql.discriminator.DiscriminatorType;
import net.firejack.platform.core.config.translate.sql.discriminator.IDiscriminatorColumnStrategy;
import net.firejack.platform.core.utils.SecurityHelper;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;


public abstract class DefaultSqlNameResolver implements ISqlNameResolver {

    private static final Logger logger = Logger.getLogger(DefaultSqlNameResolver.class);

    protected static final int CONSTRAINT_NAME_MAX_LENGTH = 30;

    protected static final String TOKEN_COMPOUND_KEY_PREFIX = "uk_";
    protected static final String TOKEN_FOREIGN_KEY_PREFIX = "fk_";
    public static final String ID_PREFIX = "id_";
    protected static final String TOKEN_ID_SUFFIX = "_id";

    private static final String TOKEN_CREATED_COLUMN = "created";
    private static final String TOKEN_WEIGHT_COLUMN = "weight";
    private static final String TOKEN_PARENT_ID_COLUMN = "parent_id";
    private static final String TOKEN_ID_COLUMN = "id";
    private static final String TOKEN_DISCRIMINATOR_COLUMN = "type";
    private static final String TOKEN_FK_COLUMN = "fk";

    private static final String MSG_FAILED_TO_FIND_TYPE = "Failed to find type by reference specified.";

    private IDiscriminatorColumnStrategy discriminatorColumnStrategy;


    public String getTokenUnderscore() {
        return TOKEN_UNDERSCORE;
    }

    @Override
    public String resolveReferenceColumn(String referencedTable) {
        return StringUtils.changeWhiteSpacesWithSymbol(
                referencedTable, getTokenUnderscore()).toLowerCase() + TOKEN_ID_SUFFIX;
    }

	@Override
    public String resolveReference(Reference reference) {
	    String referencedEntityName;
	    if (StringUtils.isNotBlank(reference.getRefName())) {
	        referencedEntityName = StringUtils.changeWhiteSpacesWithSymbol(reference.getRefName(), TOKEN_UNDERSCORE).toLowerCase();
        } else {
	        int lastDotPos = reference.getRefPath().lastIndexOf('.');
	        referencedEntityName = ID_PREFIX + (lastDotPos >= 0 ? reference.getRefPath().substring(lastDotPos + 1) : reference.getRefPath());
        }
	    return StringUtils.changeWhiteSpacesWithSymbol(referencedEntityName, TOKEN_UNDERSCORE).toLowerCase();
    }

    @Override
    public String resolveCreatedColumn() {
        return TOKEN_CREATED_COLUMN;
    }

    @Override
    public String resolveFKName(String fkTable, String pkTable) {
        if (fkTable.length() + pkTable.length() > CONSTRAINT_NAME_MAX_LENGTH - 4) {
            int nameLength = (CONSTRAINT_NAME_MAX_LENGTH - 4) / 2;
            fkTable = fkTable.length() > nameLength ? fkTable.substring(0, nameLength) : fkTable;
            pkTable = pkTable.length() > nameLength ? pkTable.substring(0, nameLength) : pkTable;
        }
        String fkName = new StringBuilder(TOKEN_FOREIGN_KEY_PREFIX).append(fkTable)
                .append(getTokenUnderscore()).append(pkTable).toString().replaceAll("_+", "_");
        fkName = fkName.length() > 23 ? fkName.substring(0, 23) : fkName;
        fkName = fkName + getTokenUnderscore() + SecurityHelper.generateRandomSequence(6).toLowerCase();
        return fkName;
    }

//    @Override
//    public String resolveFKName(IRelationshipElement rel, EntityElementManager entityManager) {
//        if (rel.getType() != RelationshipType.TYPE && rel.getType() != RelationshipType.LINK ) {
//            throw new IllegalArgumentException();
//        }
//        IEntityElement source;
//        IEntityElement target;
//        try {
//            source = entityManager.lookup(rel.getSource());
//            target = entityManager.lookup(rel.getTarget());
//        } catch (TypeLookupException e) {
//            logger.error(e.getMessage(), e);
//            throw new IllegalStateException(MSG_FAILED_TO_FIND_TYPE);
//        }
//        return StringUtils.isBlank(rel.getName()) ?
//                resolveFKName(source.getName(), target.getName()) :
//                rel.getName();
//    }
//
//    @Override
//    public String resolveUKName(String table, int index) {
//        return TOKEN_COMPOUND_KEY_PREFIX + table + index;
//    }

    @Override
    public String resolveWeightColumn() {
        return TOKEN_WEIGHT_COLUMN;
    }

    @Override
    public String resolveParentColumn() {
        return TOKEN_PARENT_ID_COLUMN;
    }

    @Override
    public String resolveIdColumn() {
        return TOKEN_ID_COLUMN;
    }

    @Override
    public String resolveDiscriminatorColumn() {
        return TOKEN_DISCRIMINATOR_COLUMN;
    }

    @Override
    public String resolveDiscriminatorValue(String entityName) {
        if (discriminatorColumnStrategy == null) {
            DiscriminatorStrategyFactory strategyFactory = DiscriminatorStrategyFactory.getInstance();
            Map<String, String> predefinedValues = new HashMap<String, String>();//todo: externalize
            predefinedValues.put("Package", "PKG");
            predefinedValues.put("Relationship", "RSP");
            predefinedValues.put("TextResource", "TXT");
            predefinedValues.put("TextResourceVersion", "TXT");
            discriminatorColumnStrategy = strategyFactory.populateDefaultDiscriminatorStrategy(
                    DiscriminatorType.STRING, predefinedValues);
        }
        return discriminatorColumnStrategy.getDiscriminatorValue(entityName);
    }

    @Override
    public String resolveTableName(IEntityElement entity) {
        List<String> tableNamePrefixes = new ArrayList<String>();
        calcTableNamePrefixes(entity, tableNamePrefixes);
        String entityNameSuffix = StringUtils.changeWhiteSpacesWithSymbol(
                entity.getName(), getTokenUnderscore()).toLowerCase();
        
        String tableName;
        if (tableNamePrefixes.isEmpty()) {
            tableName = entityNameSuffix;
        } else {
            StringBuilder sb = new StringBuilder();
            for (String tablePrefix : tableNamePrefixes) {
                sb.append(tablePrefix).append('_');
            }
            sb.append(entityNameSuffix);
            tableName = sb.toString();
        }
        return tableName;
    }

    @Override
    public String resolveColumnName(String fieldName) {
        return fieldName == null ? null :
                StringUtils.changeWhiteSpacesWithSymbol(
                        fieldName, getTokenUnderscore()).toLowerCase();
    }

    @Override
    public String resolveColumnName(IFieldElement fieldElement) {
        return fieldElement == null ? null :
                StringUtils.changeWhiteSpacesWithSymbol(
                        fieldElement.getName(), getTokenUnderscore()).toLowerCase();
    }

    @Override
    public String resolveRelationshipDBName(Reference reference, String relationshipName) {
        return TOKEN_FK_COLUMN + '_' + relationshipName.replaceAll("[\\s\\p{Punct}]+", "_").toLowerCase();
    }

    @Override
    public String resolveRelationshipTableName(IRelationshipElement relationshipElement) {
        return relationshipElement.getName().replaceAll("[\\s\\p{Punct}]+", "_").toLowerCase();
    }

    protected void calcTableNamePrefixes(INamedPackageDescriptorElement target, List<String> prefixList) {
        Map<String, String> prefixes = new LinkedHashMap<String, String>();
        calcTableNamePrefixes(target, prefixes);
        prefixList.addAll(prefixes.values());
    }

    protected void calcTableNamePrefixes(INamedPackageDescriptorElement target, Map<String, String> prefixes) {
        if (target instanceof IParentReferenceOwner) {
            IParentReferenceOwner parentReferenceOwner = (IParentReferenceOwner) target;
            INamedPackageDescriptorElement parent = parentReferenceOwner.getParent();
            if (parent != null) {
                calcTableNamePrefixes(parent, prefixes);
            }
        }
        if (target instanceof IRootElementContainer) {
            IRootElementContainer elementContainer = (IRootElementContainer) target;
            if (StringUtils.isNotBlank(elementContainer.getPrefix())) {
                prefixes.put(target.getClass().getSimpleName(), elementContainer.getPrefix());
            }
        }
    }
}