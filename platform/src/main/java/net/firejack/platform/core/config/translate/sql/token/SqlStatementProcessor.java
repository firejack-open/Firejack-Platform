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


public class SqlStatementProcessor extends SqlToken {

    private static final String SELECT = "SELECT ";
    private static final String UPDATE = "UPDATE ";
    private static final String FROM = "FROM ";
    private static final String DELETE_FROM = "DELETE FROM ";
    private static final String SET = " SET ";
    private static final String WHERE = "WHERE ";

    /**
     * @return
     */
    public SqlStatementProcessor select() {
        return append(SELECT);
    }

    /**
     * @param values
     * @return
     */
    public SqlStatementProcessor select(String... values) {
        return select().values(values).space();
    }

    /**
     * @return
     */
    public SqlStatementProcessor update() {
        return append(UPDATE);
    }

    /**
     * @param updateTarget
     * @return
     */
    public SqlStatementProcessor update(String updateTarget) {
        return update().append(updateTarget).append(SET);
    }

    /**
     * @return
     */
    public SqlStatementProcessor deleteFrom() {
        return append(DELETE_FROM);
    }

    /**
     * @param values
     * @return
     */
    public SqlStatementProcessor deleteFrom(String... values) {
        return deleteFrom().values(values).space();
    }

    /**
     * @return
     */
    public SqlStatementProcessor from() {
        return append(FROM);
    }

    /**
     * @param values
     * @return
     */
    public SqlStatementProcessor from(String... values) {
        return from().values(values).space();
    }

    /**
     * @return
     */
    public SqlStatementProcessor where() {
        return append(WHERE);
    }

    /**
     * @param condition
     * @return
     */
    public SqlStatementProcessor where(String condition) {
        return where().append(condition);
    }

    @Override
    public SqlStatementProcessor append(Object obj) {
        return (SqlStatementProcessor) super.append(obj);
    }

    @Override
    public SqlStatementProcessor space() {
        return (SqlStatementProcessor) super.space();
    }

    @Override
    public SqlStatementProcessor values(String... values) {
        return (SqlStatementProcessor) super.values(values);
    }
}