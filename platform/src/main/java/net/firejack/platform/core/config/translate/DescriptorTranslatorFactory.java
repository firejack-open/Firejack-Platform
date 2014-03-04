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

import net.firejack.platform.core.config.patch.IPatchContext;
import net.firejack.platform.model.upgrader.dbengine.DialectType;

import java.util.List;


public class DescriptorTranslatorFactory {

    private static DescriptorTranslatorFactory instance;

    private DescriptorTranslatorFactory() {
    }

    /**
     * @param sqlDialect
     * @return
     */
    public AbstractUpdateTranslator<List<String>, SqlTranslationResult> getSqlTranslator(DialectType sqlDialect) {
        return getSqlTranslator(sqlDialect, null);
    }

    /**
     * @param sqlDialect sql dialect to use
     * @param patchContext patch context wrapper object
     * @return translator initialized with proper sql dialect and context parameters
     */
    public AbstractUpdateTranslator<List<String>, SqlTranslationResult> getSqlTranslator(
            DialectType sqlDialect, IPatchContext patchContext) {
        SqlUpdateTranslator sqlTranslator = new SqlUpdateTranslator(patchContext);
        sqlTranslator.setDialectType(sqlDialect);
        return sqlTranslator;
    }

    /**
     * @return
     */
    public static DescriptorTranslatorFactory getInstance() {
        if (instance == null) {
            instance = new DescriptorTranslatorFactory();
        }
        return instance;
    }
}