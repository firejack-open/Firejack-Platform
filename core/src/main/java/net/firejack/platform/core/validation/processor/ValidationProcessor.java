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

package net.firejack.platform.core.validation.processor;

import net.firejack.platform.core.domain.BaseEntity;
import net.firejack.platform.core.utils.ClassUtils;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.validation.IMessageRuleProcessor;
import net.firejack.platform.core.validation.ValidationMessage;
import net.firejack.platform.core.validation.annotation.ValidationMode;
import net.firejack.platform.core.validation.exception.PropertyNotFoundException;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component("annotatedValidationProcessor")
public class ValidationProcessor implements IValidationProcessor {

    @Autowired
    private List<IMessageRuleProcessor> validateProcessors;

    @Override
    public List<ValidationMessage> validateArguments(Object request) throws RuleValidationException {
        return validateArguments(request, null);
    }

    public List<ValidationMessage> validateArguments(Object request, String parentProperty) throws RuleValidationException {
        Map<String, Method> readMethods = ClassUtils.getReadMethods(request);
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        for (String property : readMethods.keySet()) {
            Method readMethod = readMethods.get(property);
            Class<?> returnType = readMethod.getReturnType();
            Object value;
            try {
                value = ClassUtils.getPropertyValue(request, property, returnType);
            } catch (PropertyNotFoundException e) {
                throw new RuleValidationException("Property supposed to be found", e);
            }

            ValidationMode mode = value instanceof BaseEntity && ((BaseEntity) value).getId() != null ?
                    ValidationMode.UPDATE : ValidationMode.CREATE;

            property = StringUtils.isBlank(parentProperty) ? property : parentProperty + "." + property;
            for (IMessageRuleProcessor validateProcessor : validateProcessors) {
                List<ValidationMessage> validMessages = validateProcessor.validate(readMethod, property, value, mode);
                if (validMessages != null && !validMessages.isEmpty()) {
                    validationMessages.addAll(validMessages);
                }
            }
        }
        return validationMessages;
    }

}