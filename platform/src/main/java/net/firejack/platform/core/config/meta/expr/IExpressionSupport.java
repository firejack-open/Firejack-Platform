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

package net.firejack.platform.core.config.meta.expr;

import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.utils.IHasName;


public interface IExpressionSupport<T> {

    //binary expressions

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T eq(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T notEq(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T and(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T or(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T xor(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T greaterThan(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T greaterOrEqualThan(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T lessThan(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T lessOrEqualThan(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T add(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T subtract(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T multiply(T expr1, T expr2);

    /**
     * @param expr1
     * @param expr2
     * @return
     */
    T divide(T expr1, T expr2);

    //unary expressions

    /**
     * @param expr
     * @return
     */
    T not(T expr);

    /**
     * @param expr
     * @return
     */
    T isNull(T expr);

    /**
     * @param expr
     * @return
     */
    T isNotNull(T expr);
    ///////////////////

    /////////Misc. methods

    /**
     * @param expr
     * @return
     */
    T wrapWithBraces(T expr);

    /**
     * @param value
     * @return
     */
    T value(Object value);

    /**
     * @param function
     * @param parameters
     * @return
     */
    T function(ExpressionFunctions function, Object... parameters);

    /**
     * @param parentEntity
     * @param field
     * @param alias
     * @return
     */
    T identifier(IHasName parentEntity, IFieldElement field, String alias);

    /**
     * @param target
     * @param enumerableRanges
     * @return
     */
    T in(T target, T[] enumerableRanges);

}