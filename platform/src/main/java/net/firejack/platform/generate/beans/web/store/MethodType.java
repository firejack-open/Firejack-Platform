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

package net.firejack.platform.generate.beans.web.store;

import net.firejack.platform.api.registry.model.FieldType;

import static net.firejack.platform.api.registry.model.FieldType.*;

public enum MethodType {
    create(false, OBJECT),
    update(false, OBJECT),
    delete(false, FLAG),
    read(true, OBJECT),
    readAll(true, LIST),
    readAllWithFilter(true, LIST),
    search(true, LIST),
    searchCount(true, INTEGER_NUMBER),
    searchWithFilter(true, LIST),
    searchCountWithFilter(true, INTEGER_NUMBER),
    advancedSearch(true, LIST),
    advancedSearchCount(true, INTEGER_NUMBER),
    advancedSearchWithIdsFilter(true, LIST),
    advancedSearchCountWithIdsFilter(true, INTEGER_NUMBER);

    private boolean readOnly;
    private FieldType returnType;

    private MethodType(boolean readOnly, FieldType returnType) {
        this.readOnly = readOnly;
        this.returnType = returnType;
    }

    MethodType(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @return
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    public FieldType getReturnType() {
        return returnType;
    }

    /**
     * @param name
     * @return
     */
    public static MethodType find(String name) {
        try {
            return MethodType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
