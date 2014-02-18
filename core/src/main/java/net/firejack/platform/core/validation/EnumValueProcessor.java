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
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.validation.annotation.EnumValue;
import net.firejack.platform.core.validation.annotation.ValidationMode;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import net.firejack.platform.core.validation.constraint.vo.RuleParameter;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Component
public class EnumValueProcessor implements IMessageRuleProcessor {

	private static final String ENUM_MESSAGE_NAME_SUFFIX = ".name";
	private static final String ENUM_MESSAGE_DESCRIPTION_SUFFIX = ".description";

	@Override
	public List<ValidationMessage> validate(Method readMethod, String property, Object value, ValidationMode mode)
			throws RuleValidationException {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		EnumValue checkEnumValue = readMethod.getAnnotation(EnumValue.class);
		if (checkEnumValue != null) {
			String parameterName = StringUtils.isNotBlank(checkEnumValue.parameterName()) ?
					checkEnumValue.parameterName() : property;
			if (value == null && !checkEnumValue.nullable()) {
				validationMessages.add(new ValidationMessage(property, checkEnumValue.msgNotBlankKey(), parameterName));
			} else if (value != null) {
				Class<? extends Enum> clazz = checkEnumValue.enumClass();
				if (clazz.isAssignableFrom(value.getClass())) {
					return validationMessages;
				}

				Enum[] enumValues = clazz.getEnumConstants();
				boolean isContains = false;
				for (Enum enumValue : enumValues) {
					if (enumValue.name().equals(value)) {
						isContains = true;
						break;
					}
				}
				if (!isContains) {
					validationMessages.add(new ValidationMessage(property, checkEnumValue.msgKey(), parameterName));
				}
			}
		}
		return validationMessages;
	}

	@Override
	public List<Constraint> generate(Method readMethod, String property, Map<String, String> params) {
		List<Constraint> constraints = null;
		EnumValue enumValue = readMethod.getAnnotation(EnumValue.class);
		if (enumValue != null) {
			Constraint constraint = new Constraint(enumValue.annotationType().getSimpleName());
			List<RuleParameter> ruleParameters = new ArrayList<RuleParameter>();

			boolean hasName = enumValue.hasName();
			boolean hasDescription = enumValue.hasDescription();
			int length = hasDescription ? 3 : 2;

			RuleParameter ruleParameter = new RuleParameter("hasDescription", hasDescription);
			ruleParameters.add(ruleParameter);

			Class<? extends Enum> enumClass = enumValue.enumClass();
			Enum[] enumValues = enumClass.getEnumConstants();
			String name = enumClass.getName();
			String[][] values = new String[enumValues.length][length];
			for (int i = 0, enumValuesLength = enumValues.length; i < enumValuesLength; i++) {
				values[i][0] = enumValues[i].name();
				if (hasName) {
					String messageKey = name + "." + enumValues[i].name() + ENUM_MESSAGE_NAME_SUFFIX;
					values[i][1] = MessageResolver.messageFormatting(messageKey, Locale.ENGLISH); //TODO need to set real locale
				} else {
					values[i][1] = enumValues[i].toString();
				}
				if (hasDescription) {
					String messageKey = name + "." + enumValues[i].name() + ENUM_MESSAGE_DESCRIPTION_SUFFIX;
					values[i][2] = MessageResolver.messageFormatting(messageKey, Locale.ENGLISH); //TODO need to set real locale
				}
			}
			RuleParameter enumValuesParam = new RuleParameter("values", values);
			ruleParameters.add(enumValuesParam);
            String errorMessage = MessageResolver.messageFormatting(enumValue.msgKey(), Locale.ENGLISH, property); //TODO need to set real locale
            constraint.setErrorMessage(errorMessage);
			constraint.setParams(ruleParameters);
			constraints = new ArrayList<Constraint>();
			constraints.add(constraint);
		}
		return constraints;
	}

}