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

import net.firejack.platform.core.config.meta.IEntityElement;
import net.firejack.platform.core.config.meta.IFieldElement;
import net.firejack.platform.core.config.meta.parse.Alias;
import net.firejack.platform.core.config.translate.sql.ISqlDialect;
import net.firejack.platform.core.utils.IHasName;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.utils.Tuple;

import java.util.HashMap;
import java.util.Map;


public class ExpressionFactory<T> {

    private static Map<ISqlDialect, ExpressionFactory<String>> sqlFactories =
            new HashMap<ISqlDialect, ExpressionFactory<String>>();

    private Class<T> evaluationClass;
    private IExpressionSupport<T> expressionSupport;

    private ExpressionFactory(Class<T> evaluationClass, IExpressionSupport<T> expressionSupport) {
        this.evaluationClass = evaluationClass;
        this.expressionSupport = expressionSupport;
    }

    /**
     * @return
     */
    public IExpressionContext<T> buildExpressionContext() {
        return new ExpressionContext<T>(expressionSupport);
    }

    private Tuple<IExpression<T>, Integer> checkExpr(int parametersOffset, Object parameter, Object... additionalParameters) {
        if (additionalParameters.length > parametersOffset && !(parameter instanceof IExpression)) {
            if (parameter instanceof IFieldElement) {
                if (!(additionalParameters[parametersOffset] instanceof Alias) &&
                        !(additionalParameters[parametersOffset] instanceof IEntityElement)) {
                    throw new IllegalArgumentException();
                }
                return new Tuple<IExpression<T>, Integer>(
                        expr(parameter, additionalParameters[parametersOffset++]),
                        parametersOffset);
            }
        } else {
            return new Tuple<IExpression<T>, Integer>(expr(parameter), parametersOffset);
        }
        throw new IllegalArgumentException();
    }

    /**
     * @param type
     * @param param
     * @param additionalParameters
     * @return
     */
    public IExpression<T> buildExpression(ExpressionType type, Object param, Object... additionalParameters) {
        if (type == null || param == null) {
            throw new IllegalArgumentException();
        }
        if (type.isBinary()) {
            if (additionalParameters.length > 0) {
                int parametersOffset = 0;
                Tuple<IExpression<T>, Integer> exprTuple = checkExpr(parametersOffset, param, additionalParameters);
                IExpression<T> expr1 = exprTuple.getKey();
                parametersOffset = exprTuple.getValue();
                parametersOffset++;
                exprTuple = checkExpr(parametersOffset, additionalParameters[parametersOffset - 1], additionalParameters);
                IExpression<T> expr2 = exprTuple.getKey();
                return new BinaryExpression<T>(type, expr1, expr2);
            }
        } else if (type.isUnary()) {
            return new UnaryExpression<T>(type, expr(param));
        } else if (type.equals(ExpressionType.IN)) {
            if (additionalParameters.length > 0) {
                @SuppressWarnings("unchecked")
                IExpression<T>[] expressions = new IExpression[additionalParameters.length];
                for (int i = 0; i < additionalParameters.length; i++) {
                    expressions[i] = expr(additionalParameters);
                }
                return new InExpression<T>(expr(param), expressions);
            }
        } else if (type.equals(ExpressionType.IDENTIFIER)) {
            if (param instanceof IFieldElement) {
                IFieldElement field = (IFieldElement) param;
                if (StringUtils.isNotBlank(field.getName())) {
                    if (additionalParameters.length > 0 && (additionalParameters[0] instanceof Alias ||
                            additionalParameters[0] instanceof IEntityElement)) {
                        IHasName parentEntity = (IHasName) additionalParameters[0];
                        String alias = additionalParameters.length > 1 &&
                                additionalParameters[1] instanceof String ? (String) additionalParameters[1] : null;
                        return new IdentifiableExpression<T>(parentEntity, field, alias);
                    }
                }
            }
        } else if (type.equals(ExpressionType.BRACES)) {
            return new InBracesExpression<T>(expr(param));
        } else if (type.equals(ExpressionType.FUNCTION) && param instanceof ExpressionFunctions) {
            ExpressionFunctions function = (ExpressionFunctions) param;
            return new FunctionExpression<T>(function, additionalParameters);
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    private IExpression<T> expr(Object expression, Object... parameters) {
        if (expression instanceof IFieldElement) {
            if (parameters != null && parameters.length > 0 && parameters[0] instanceof IHasName) {
                return expr(buildExpression(ExpressionType.IDENTIFIER, expression, parameters[0]));
            }
        } else if (expression instanceof IExpression) {
            return (IExpression<T>) expression;
            /*Type[] actualTypeArguments = ((ParameterizedType)
                                   expression.getClass().getGenericSuperclass()).getActualTypeArguments();
                           if (actualTypeArguments.length >=1 && actualTypeArguments[0] == evaluationClass) {
                               return (IExpression<T>) expression;
                           }*/
            /*I
                           //todo: review
                           Expression expr = (IExpression) expression;
                           List<Class<?>> classes = ClassUtils.getTypeArguments(IExpression.class, expr.getClass());
                           if (classes.size() != 0 && classes.get(0) == evaluationClass) {
                               return (IExpression<T>) expression;
                           }*/
        } else if (expression instanceof String) {
            return new ValueExpression<T>(expression);
        }
        throw new IllegalArgumentException();
    }

    /**
     * @param dialect
     * @return
     */
    public static ExpressionFactory<String> getSqlExpressionInstance(ISqlDialect dialect) {
        ExpressionFactory<String> sqlExprFactory = sqlFactories.get(dialect);
        if (sqlExprFactory == null) {
            sqlExprFactory = new ExpressionFactory<String>(String.class, dialect);
            sqlFactories.put(dialect, sqlExprFactory);
        }
        return sqlExprFactory;
    }
}