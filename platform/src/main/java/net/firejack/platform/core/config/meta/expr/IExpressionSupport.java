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