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