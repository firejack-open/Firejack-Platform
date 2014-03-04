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
