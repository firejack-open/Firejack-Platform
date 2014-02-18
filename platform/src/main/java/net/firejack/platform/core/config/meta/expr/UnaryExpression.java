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