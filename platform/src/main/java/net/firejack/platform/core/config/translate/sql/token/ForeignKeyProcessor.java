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

package net.firejack.platform.core.config.translate.sql.token;

import net.firejack.platform.api.registry.model.RelationshipOption;
import net.firejack.platform.core.config.translate.sql.ISqlSupport;


public class ForeignKeyProcessor extends SqlToken {

    /***/
    public ForeignKeyProcessor() {
    }

    /**
     * @param sb
     */
    public ForeignKeyProcessor(StringBuilder sb) {
        super(sb);
    }

    /**
     * @param sqlSupport
     */
    public void setSqlDialect(ISqlSupport sqlSupport) {
        this.sqlSupport = sqlSupport;
    }

    /**
     * @param options
     * @return
     */
    public ForeignKeyProcessor addOnUpdateOption(RelationshipOption options) {
        this.sqlSupport.addOnUpdateSection(this, options);
        return this;
    }

    /**
     * @param options
     * @return
     */
    public ForeignKeyProcessor addOnDeleteOption(RelationshipOption options) {
        this.sqlSupport.addOnDeleteSection(this, options);
        return this;
    }

    @Override
    public ForeignKeyProcessor append(Object obj) {
        return (ForeignKeyProcessor) super.append(obj);
    }

    @Override
    public ForeignKeyProcessor space() {
        return (ForeignKeyProcessor) super.space();
    }

    @Override
    public ForeignKeyProcessor inBraces(Object obj) {
        return (ForeignKeyProcessor) super.inBraces(obj);
    }
}