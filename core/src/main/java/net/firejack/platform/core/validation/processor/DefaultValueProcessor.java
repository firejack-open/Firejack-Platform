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
