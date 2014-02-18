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


import net.firejack.platform.core.utils.ArrayUtils;
import net.firejack.platform.core.utils.MessageResolver;
import net.firejack.platform.core.validation.annotation.Length;
import net.firejack.platform.core.validation.annotation.ValidationMode;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import net.firejack.platform.core.validation.constraint.vo.RuleParameter;
import net.firejack.platform.core.validation.exception.ImproperValidationArgumentException;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Component
public class LengthProcessor implements IMessageRuleProcessor {

//    @Autowired
//    private MessageResolver messageResolver;

    @Override
    public List<ValidationMessage> validate(Method readMethod, String property, Object value, ValidationMode mode)
            throws RuleValidationException {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        Annotation annotation = readMethod.getAnnotation(Length.class);
        if (annotation != null) {
            Length length = (Length) annotation;
            if (length.minLength() > length.maxLength()) {
                throw new ImproperValidationArgumentException("Min value should be equal or less then max value.");
            }
            boolean validate = ArrayUtils.contains(length.modes(), mode);
            if (validate) {
                Class<?> returnType = readMethod.getReturnType();
                if (returnType == String.class) {
                    String sValue = (String) value;
                    String parameterName = StringUtils.isNotBlank(length.parameterName()) ?
                            length.parameterName() : property;
                    if ((StringUtils.isBlank(sValue) && length.minLength() > 0) ||
                            (StringUtils.isNotBlank(sValue) && sValue.length() < length.minLength())) {
                        validationMessages.add(
                                new ValidationMessage(property, length.minLengthMsgKey(), parameterName, length.minLength()));
                    }
                    if (StringUtils.isNotBlank(sValue) && sValue.length() > length.maxLength()) {
                        validationMessages.add(
                                new ValidationMessage(property, length.maxLengthMsgKey(), parameterName, length.maxLength()));
                    }
                } else {
                    throw new ImproperValidationArgumentException("Argument should be of type java.lang.String");
                }
            }
        }
        return validationMessages;
    }

    @Override
    public List<Constraint> generate(Method readMethod, String property, Map<String, String> params) {
        List<Constraint> constraints = null;
        Annotation annotation = readMethod.getAnnotation(Length.class);
        if (annotation != null) {
            Length length = (Length) annotation;

            boolean generateConstraint;
            if (params != null && StringUtils.isNotBlank(params.get("id"))) {
                generateConstraint = ArrayUtils.contains(length.modes(), ValidationMode.UPDATE);
            } else {
                generateConstraint = ArrayUtils.contains(length.modes(), ValidationMode.CREATE);
            }
            if (generateConstraint) {
                String parameterName = StringUtils.isNotBlank(length.parameterName()) ?
                            length.parameterName() : property;
                Constraint constraint = new Constraint(length.annotationType().getSimpleName());
                List<RuleParameter> ruleParameters = new ArrayList<RuleParameter>();
                RuleParameter minParam = new RuleParameter("min", length.minLength());
                ruleParameters.add(minParam);
                String minLengthMsg = MessageResolver.messageFormatting(
                        length.minLengthMsgKey(), Locale.ENGLISH, parameterName, length.minLength()); //TODO need to set real locale
                RuleParameter minMsgParam = new RuleParameter("minMsg", minLengthMsg);
                ruleParameters.add(minMsgParam);
                RuleParameter maxParam = new RuleParameter("max", length.maxLength());
                ruleParameters.add(maxParam);
                String maxLengthMsg = MessageResolver.messageFormatting(
                        length.maxLengthMsgKey(), Locale.ENGLISH, parameterName, length.maxLength()); //TODO need to set real locale
                RuleParameter maxMsgParam = new RuleParameter("maxMsg", maxLengthMsg);
                ruleParameters.add(maxMsgParam);
                constraint.setParams(ruleParameters);
                constraints = new ArrayList<Constraint>();
                constraints.add(constraint);
            }
        }
        return constraints;
    }

}
