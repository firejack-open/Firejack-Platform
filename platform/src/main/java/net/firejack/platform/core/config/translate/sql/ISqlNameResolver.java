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
import net.firejack.platform.core.config.meta.IRelationshipElement;
import net.firejack.platform.core.config.meta.construct.Reference;


public interface ISqlNameResolver {

    public static final String TOKEN_UNDERSCORE = "_";

    /**
     * @param reference
     * @return
     */
    String resolveReference(Reference reference);

    /**
     * @param referencedTable
     * @return
     */
    String resolveReferenceColumn(String referencedTable);

    /**
     * @return
     */
    String resolveCreatedColumn();

    /**
     * @param fkTable
     * @param pkTable
     * @return
     */
    String resolveFKName(String fkTable, String pkTable);

//    /**
//     * @param rel
//     * @param entityManager
//     * @return
//     */
//    String resolveFKName(IRelationshipElement rel, EntityElementManager entityManager);

//    /**
//     * @param table
//     * @param index
//     * @return
//     */
//    String resolveUKName(String table, int index);

    /**
     * @return
     */
    String resolveWeightColumn();

    /**
     * @return
     */
    String resolveParentColumn();

    /**
     * @return
     */
    String resolveIdColumn();

    /**
     * @return
     */
    String resolveDiscriminatorColumn();

    /**
     * @param entityName
     * @return
     */
    String resolveDiscriminatorValue(String entityName);

    /**
     * @param entity
     * @return
     */
    String resolveTableName(IEntityElement entity);

    String resolveColumnName(String fieldName);

    /**
     * @param fieldElement
     * @return
     */
    String resolveColumnName(IFieldElement fieldElement);

    /**
     * @param relationshipElement
     * @return
     */
    String resolveRelationshipDBName(Reference reference, String relationshipName);

	String resolveRelationshipTableName(IRelationshipElement relationshipElement);
}