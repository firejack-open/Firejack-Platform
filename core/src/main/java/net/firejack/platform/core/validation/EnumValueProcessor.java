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