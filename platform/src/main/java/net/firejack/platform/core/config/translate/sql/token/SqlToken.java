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

import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.translate.sql.ISqlSupport;


public class SqlToken {

    protected StringBuilder sb;
    protected ISqlSupport sqlSupport;

    /***/
    public SqlToken() {
        sb = new StringBuilder();
    }

    /**
     * @param sb
     */
    public SqlToken(StringBuilder sb) {
        this.sb = sb;
    }

    /**
     * @param obj
     * @return
     */
    public SqlToken append(Object obj) {
        sb.append(obj);
        return this;
    }

    /**
     * @return
     */
    public SqlToken space() {
        return append(" ");
    }

    /**
     * @param obj
     * @return
     */
    public SqlToken inBraces(Object obj) {
        return append('(').append(obj).append(')');
    }

    /**
     * @param values
     * @return
     */
    public SqlToken values(String... values) {
        if (values.length != 0) {
            append(values[0]);
            for (int i = 1; i < values.length; i++) {
                append(", ").append(values[i]);
            }
        }
        return this;
    }

    /**
     * @param values
     * @return
     */
    public SqlToken valuesInBraces(String... values) {
        if (values.length != 0) {
            append('(').values(values).append(')');
        }
        return this;
    }

    /**
     * @param field
     * @param value
     * @return
     */
    public SqlToken value(IFieldElement field, String value) {
        if (field == null) {
            return this;
        }
        if (value == null) {
            return append("NULL");
        } else if (field.getType().isString()) {
            return append('\'').append(value).append('\'');
        } else {
            return append(value);
        }
    }

    /**
     * @return
     */
    public String statement() {
        return sb.toString();
    }
}