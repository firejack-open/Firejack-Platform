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


import net.firejack.platform.core.utils.MessageResolver;
import net.firejack.platform.core.validation.annotation.Match;
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
public class MatchProcessor implements IMessageRuleProcessor {

    private Map<String, Pattern> cachedPatterns;
    private static final Logger logger = Logger.getLogger(MatchProcessor.class);

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
    public List<ValidationMessage> validate(Method readMethod, String property, Object value, ValidationMode mode)
            throws RuleValidationException {
        Match matchAnnotation = readMethod.getAnnotation(Match.class);
        if (matchAnnotation != null && StringUtils.isNotBlank(matchAnnotation.expression())) {
            Class<?> returnType = readMethod.getReturnType();
            if (returnType == String.class) {
                Pattern pattern = getCachedPatterns().get(matchAnnotation.expression());
                if (pattern == null) {
                    try {
                        pattern = Pattern.compile(matchAnnotation.expression());
                        getCachedPatterns().put(matchAnnotation.expression(), pattern);
                    } catch (PatternSyntaxException e) {
                        logger.error(e.getMessage(), e);
                        throw new ImproperValidationArgumentException("Pattern expression should have correct syntax.");
                    }
                }
                List<ValidationMessage> messages = null;
                if (value != null) {
                    String sValue = (String) value;
                    if (StringUtils.isNotBlank(sValue) && !pattern.matcher(sValue).matches()) {
                        messages = new ArrayList<ValidationMessage>();
                        messages.add(new ValidationMessage(property,
                                matchAnnotation.msgKey(), matchAnnotation.parameterName()));
                    }
                }
                return messages;
            }

        }
        return null;
    }

    @Override
    public List<Constraint> generate(Method readMethod, String property, Map<String, String> params) {
        List<Constraint> constraints = null;
        Match match = readMethod.getAnnotation(Match.class);
        if (match != null) {
            Constraint constraint = new Constraint(match.annotationType().getSimpleName());
            String errorMessage = MessageResolver.messageFormatting(match.msgKey(), Locale.ENGLISH, property, match.example()); //TODO need to set real locale
            constraint.setErrorMessage(errorMessage);
            List<RuleParameter> ruleParameters = new ArrayList<RuleParameter>();
            RuleParameter expressionParam = new RuleParameter("expression", match.expression());
            ruleParameters.add(expressionParam);
            constraint.setParams(ruleParameters);
            constraints = new ArrayList<Constraint>();
            constraints.add(constraint);
        }
        return constraints;
    }

}