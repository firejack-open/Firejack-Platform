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

import net.firejack.platform.core.utils.OpenFlameSpringContext;
import net.firejack.platform.core.validation.annotation.Condition;
import net.firejack.platform.core.validation.annotation.ValidationMode;
import net.firejack.platform.core.validation.condition.AbstractCondition;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class ConditionProcessor implements IMessageRuleProcessor {

    private static final Logger logger = Logger.getLogger(ConditionProcessor.class);

    @Override
    public List<ValidationMessage> validate(Method readMethod, String property, Object value, ValidationMode mode)
            throws RuleValidationException {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        Annotation annotation = readMethod.getAnnotation(Condition.class);
        if (annotation != null) {
            Condition condition = (Condition) annotation;
            try {
                AbstractCondition voCondition = (AbstractCondition) OpenFlameSpringContext.getBean(condition.value());
                List<ValidationMessage> vMessages = voCondition.validate(value);
                if (vMessages != null) {
                    validationMessages = vMessages;
                }
            } catch (NoSuchBeanDefinitionException e) {
                logger.warn(e.getMessage());
            }
        }
        return validationMessages;
    }

    @Override
    public List<Constraint> generate(Method readMethod, String property, Map<String, String> params) {
        List<Constraint> constraintVOs = null;
        Annotation annotation = readMethod.getAnnotation(Condition.class);
        if (annotation != null) {
            Condition condition = (Condition) annotation;
            try {
                AbstractCondition voCondition = (AbstractCondition) OpenFlameSpringContext.getBean(condition.value());
                constraintVOs = voCondition.generateConstraints(params);
            } catch (NoSuchBeanDefinitionException e) {
                logger.warn(e.getMessage());
            }
        }
        return constraintVOs;
    }

}
