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