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

import net.firejack.platform.core.validation.annotation.ValidateNested;
import net.firejack.platform.core.validation.annotation.ValidationMode;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import net.firejack.platform.core.validation.processor.ValidationProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class ValidateNestedProcessor implements IMessageRuleProcessor {
    @Autowired
    private ValidationProcessor validationProcessor;

    @Override
    public List<ValidationMessage> validate(Method m, String property, Object value, ValidationMode mode) throws RuleValidationException {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        ValidateNested annotation = m.getAnnotation(ValidateNested.class);
        if (annotation != null && value != null) {
            if (annotation.excludePath())
                property = null;
            if (annotation.iterable()) {
                Iterable iterable = (Iterable) value;
                for (Object item : iterable) {
                    validationMessages.addAll(validationProcessor.validateArguments(item, property));
                }
            } else {
                validationMessages.addAll(validationProcessor.validateArguments(value, property));
            }
        }
        return validationMessages;
    }

    @Override
    public List<Constraint> generate(Method readMethod, String property, Map<String, String> params) {
        return null;
    }

}
