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

import net.firejack.platform.core.validation.annotation.NonEmptyCollection;
import net.firejack.platform.core.validation.annotation.ValidationMode;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import net.firejack.platform.core.validation.exception.ImproperValidationArgumentException;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Component
public class NonEmptyCollectionProcessor implements IMessageRuleProcessor {

    public List<ValidationMessage> validate(Method readMethod, String property, Object value, ValidationMode mode)
            throws RuleValidationException {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        Annotation annotation = readMethod.getAnnotation(NonEmptyCollection.class);
        if (annotation != null) {
            NonEmptyCollection notEmptyCollection = (NonEmptyCollection) annotation;
            String parameterName = StringUtils.isNotBlank(notEmptyCollection.parameterName()) ?
                                        notEmptyCollection.parameterName() : property;
            if (value == null) {
                validationMessages.add(new ValidationMessage(property, notEmptyCollection.msgKey(), parameterName));
            }
            if (!(value instanceof Collection)) {
                throw new ImproperValidationArgumentException("Argument should be of type java.util.Collection");
            }
            if (((Collection) value).size() == 0) {
                validationMessages.add(new ValidationMessage(property, notEmptyCollection.msgKey(), parameterName));
            }
        }
        return validationMessages;
    }

    @Override
    public List<Constraint> generate(Method readMethod, String property, Map<String, String> params) {
        return null;
    }

}