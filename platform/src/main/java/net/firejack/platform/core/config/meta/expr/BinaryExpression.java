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