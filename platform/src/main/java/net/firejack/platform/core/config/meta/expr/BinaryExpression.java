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


public class BinaryExpression<T> implements IExpression<T> {

    private ExpressionType type;
    private IExpression<T> operand1;
    private IExpression<T> operand2;

    /**
     * @param type
     * @param operand1
     * @param operand2
     */
    public BinaryExpression(ExpressionType type, IExpression<T> operand1, IExpression<T> operand2) {
        this.type = type;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public T evaluate(IExpressionContext<T> context) throws ExpressionEvaluationException {
        T exprResult1 = operand1.evaluate(context.populateContext());
        T exprResult2 = operand2.evaluate(context.populateContext());
        IExpressionSupport<T> expressionSupport = context.getExpressionSupport();
        switch (type) {
            case AND:
                return expressionSupport.and(exprResult1, exprResult2);
            case OR:
                return expressionSupport.or(exprResult1, exprResult2);
            case XOR:
                return expressionSupport.xor(exprResult1, exprResult2);
            case PLUS:
                return expressionSupport.add(exprResult1, exprResult2);
            case MINUS:
                return expressionSupport.subtract(exprResult1, exprResult2);
            case MULTIPLY:
                return expressionSupport.multiply(exprResult1, exprResult2);
            case DIVIDE:
                return expressionSupport.divide(exprResult1, exprResult2);
            case EQ:
                return expressionSupport.eq(exprResult1, exprResult2);
            case NEQ:
                return expressionSupport.notEq(exprResult1, exprResult2);
            case GT:
                return expressionSupport.greaterThan(exprResult1, exprResult2);
            case GTE:
                return expressionSupport.greaterOrEqualThan(exprResult1, exprResult2);
            case LT:
                return expressionSupport.lessThan(exprResult1, exprResult2);
            case LTE:
                return expressionSupport.lessOrEqualThan(exprResult1, exprResult2);
        }
        throw new ExpressionEvaluationException("Wrong expression type = [" + type + "] for binary expression.");
    }
}