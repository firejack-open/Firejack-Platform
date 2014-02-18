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