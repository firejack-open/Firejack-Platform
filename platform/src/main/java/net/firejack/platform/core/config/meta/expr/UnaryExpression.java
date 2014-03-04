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


public class UnaryExpression<T> implements IExpression<T> {

    private ExpressionType type;
    private IExpression<T> operand;

    /**
     * @param type
     * @param operand
     */
    public UnaryExpression(ExpressionType type, IExpression<T> operand) {
        this.type = type;
        this.operand = operand;
    }

    @Override
    public T evaluate(IExpressionContext<T> context) throws ExpressionEvaluationException {
        T exprResult = operand.evaluate(context.populateContext());
        IExpressionSupport<T> expressionSupport = context.getExpressionSupport();
        switch (type) {
            case NOT:
                return expressionSupport.not(exprResult);
            case IS_NULL:
                return expressionSupport.isNull(exprResult);
            case IS_NOT_NULL:
                return expressionSupport.isNotNull(exprResult);
        }
        throw new ExpressionEvaluationException("Wrong expression type = [" + type + "] for unary expression.");
    }
}