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

package net.firejack.platform.core.config.upgrader.model.translate;

import net.firejack.platform.core.config.meta.diff.*;
import net.firejack.platform.core.config.translate.ElementsDiffTransformer;
import net.firejack.platform.model.upgrader.dbengine.DialectType;

import java.util.Map;


public class UpgradeDiffTransformer implements ElementsDiffTransformer {

    private Map<String, DialectType> sqlDialectMapping;

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PackageDescriptorElementDiff> T transformElementsDiff(T elementsDiff) {
        T result = null;
        if (elementsDiff instanceof FieldsDiff) {
            result = (T) transformFieldsDiff((FieldsDiff) elementsDiff);
        } else if (elementsDiff instanceof EntitiesDiff) {
            result = (T) transformEntitiesDiff((EntitiesDiff) elementsDiff);
        } else if (elementsDiff instanceof IndexesDiff) {
            result = (T) transformIndexesDiff((IndexesDiff) elementsDiff);
        } else if (elementsDiff instanceof RelationshipsDiff) {
            result = (T) transformRelationshipsDiff((RelationshipsDiff) elementsDiff);
        }
        return result;
    }

    public Map<String, DialectType> getSqlDialectMapping() {
        return sqlDialectMapping;
    }

    public void setSqlDialectMapping(Map<String, DialectType> sqlDialectMapping) {
        this.sqlDialectMapping = sqlDialectMapping;
    }

    protected FieldsDiff transformFieldsDiff(FieldsDiff diff) {
        return diff;
    }

    protected EntitiesDiff transformEntitiesDiff(EntitiesDiff diff) {
        return diff;
    }

    protected RelationshipsDiff transformRelationshipsDiff(RelationshipsDiff diff) {
        return diff;
    }

    protected IndexesDiff transformIndexesDiff(IndexesDiff diff) {
        return diff;
    }

}