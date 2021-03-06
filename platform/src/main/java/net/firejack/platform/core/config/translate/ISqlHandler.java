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

package net.firejack.platform.core.config.translate;

import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.core.config.translate.sql.ISqlDialect;
import net.firejack.platform.core.config.translate.sql.ISqlNameResolver;


public interface ISqlHandler<R, TR extends AbstractTranslationResult<R>> {

    /**
     * @param keyName
     * @param sourceTableName
     * @param referenceFieldName
     * @param targetTableName
     * @param onUpdateOptions
     * @param onDeleteOptions
     */
    void addForeignKey(String keyName, String sourceTableName,
                       String referenceFieldName, String targetTableName, String targetFieldName,
                       RelationshipOption onUpdateOptions, RelationshipOption onDeleteOptions);

    /**
     * @return
     */
    ISqlNameResolver getSqlNameResolver();

    /**
     * @return
     */
    ISqlDialect getSqlSupport();

    /**
     * @return
     */
    TR getResultState();

}