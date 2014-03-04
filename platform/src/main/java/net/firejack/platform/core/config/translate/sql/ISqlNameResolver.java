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