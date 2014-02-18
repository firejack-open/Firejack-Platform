/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
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

package net.firejack.platform.core.validation;


import net.firejack.platform.core.utils.MessageResolver;
import net.firejack.platform.core.validation.annotation.NotMatch;
import net.firejack.platform.core.validation.annotation.ValidationMode;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import net.firejack.platform.core.validation.constraint.vo.RuleParameter;
import net.firejack.platform.core.validation.exception.ImproperValidationArgumentException;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


@Component
public class NotMatchProcessor implements IMessageRuleProcessor {
    private static final Logger logger = Logger.getLogger(NotMatchProcessor.class);
    public static final String[] words = new String[]{"abstract", "continue", "for", "new",
            "switch", "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if",
            "private", "this", "break", "double", "implements", "protected", "throw", "byte",
            "else", "import", "public", "throws", "case", "enum", "instanceof", "return",
            "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface",
            "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float",
            "native", "super", "while"};
    private Map<String, Pattern> cachedPatterns;

    /**
     * @return
     */
    public Map<String, Pattern> getCachedPatterns() {
        if (cachedPatterns == null) {
            cachedPatterns = new HashMap<String, Pattern>();
        }
        return cachedPatterns;
    }

    @Override
    public List<ValidationMessage> validate(Method readMethod, String property, Object value, ValidationMode mode) throws RuleValidationException {
        List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        NotMatch notMatchAnnotation = readMethod.getAnnotation(NotMatch.class);
        if (notMatchAnnotation != null && StringUtils.isNotBlank(notMatchAnnotation.expression())) {
            Class<?> returnType = readMethod.getReturnType();
            if (returnType == String.class) {
                Pattern pattern = getCachedPatterns().get(notMatchAnnotation.expression());
                if (pattern == null) {
                    try {
                        pattern = Pattern.compile(notMatchAnnotation.expression());
                        getCachedPatterns().put(notMatchAnnotation.expression(), pattern);
                    } catch (PatternSyntaxException e) {
                        logger.error(e.getMessage(), e);
                        throw new ImproperValidationArgumentException("Pattern expression should have correct syntax.");
                    }
                }
                if (value != null) {
                    String sValue = (String) value;
                    if (StringUtils.isNotBlank(sValue) && pattern.matcher(sValue).matches()) {
                        messages.add(new ValidationMessage(property, notMatchAnnotation.msgKey(), notMatchAnnotation.parameterName()));
                    }
                }
            }
        }

        if (notMatchAnnotation != null && !notMatchAnnotation.javaWords()) {
            Class<?> returnType = readMethod.getReturnType();
            if (returnType == String.class && StringUtils.isNotBlank((String) value)) {
                String s = (String) value;
                for (String word : words) {
                    if (word.equals(s)) {
                        messages.add(new ValidationMessage(property, notMatchAnnotation.msgKey(), word));
                    }
                }
            }
        }

        return messages;
    }

    @Override
    public List<Constraint> generate(Method readMethod, String property, Map<String, String> params) {
        List<Constraint> constraints = null;
        NotMatch notMatch = readMethod.getAnnotation(NotMatch.class);
        if (notMatch != null) {
            Constraint constraint = new Constraint(notMatch.annotationType().getSimpleName());
            String errorMessage = MessageResolver.messageFormatting(notMatch.msgKey(), Locale.ENGLISH, property, notMatch.example()); //TODO need to set real locale
            constraint.setErrorMessage(errorMessage);
            List<RuleParameter> ruleParameters = new ArrayList<RuleParameter>();

            String expression = notMatch.expression();
            if (StringUtils.isNotBlank(expression)) {
                RuleParameter expressionParam = new RuleParameter("expression", expression);
                ruleParameters.add(expressionParam);
            }

            if (!notMatch.javaWords()) {
                RuleParameter ruleParameter = new RuleParameter("words", words);
                ruleParameters.add(ruleParameter);
            }

            if (!ruleParameters.isEmpty()) {
                constraints = new ArrayList<Constraint>();
                constraint.setParams(ruleParameters);
                constraints.add(constraint);
            }
        }
        return constraints;
    }

}