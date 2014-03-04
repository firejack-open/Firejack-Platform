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

import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.OpenFlameSpringContext;
import net.firejack.platform.core.utils.StringUtils;
import net.firejack.platform.core.validation.annotation.Condition;
import net.firejack.platform.core.validation.annotation.DefaultValue;
import net.firejack.platform.core.validation.condition.AbstractCondition;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component("defaultValueProcessor")
public class DefaultValueProcessor {

    private static final Logger logger = Logger.getLogger(DefaultValueProcessor.class);

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("^\\$\\{([\\w\\.]+)\\}$");

    /**
     * @param method
     * @param propertyName
     * @param params
     * @return
     */
    public Object getDefaultValue(Method method, String propertyName, Map<String, String> params) {
        Object defaultValue = null;
        Condition conditionAnnotation = method.getAnnotation(Condition.class);
        DefaultValue defaultValueAnnotation = method.getAnnotation(DefaultValue.class);
        if (conditionAnnotation != null) {
            AbstractCondition voCondition = OpenFlameSpringContext.getBean(conditionAnnotation.value());
            defaultValue = voCondition.defaultValue(params);
        }
	    if (defaultValue == null && defaultValueAnnotation != null) {
		    defaultValue = defaultValueAnnotation.value();
		    String value = defaultValueAnnotation.value();
		    if (value.matches("true|false")) {
			    defaultValue = Boolean.parseBoolean(value);
		    } else {
			    Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
			    if (matcher.find()) {
				    String placeholder = matcher.group(1);
				    value = String.valueOf(ConfigContainer.get(placeholder));
				    if (StringUtils.isNotEmpty(value)) {
					    defaultValue = value;
				    }
			    }
		    }
	    }
	    return defaultValue;
    }
}
