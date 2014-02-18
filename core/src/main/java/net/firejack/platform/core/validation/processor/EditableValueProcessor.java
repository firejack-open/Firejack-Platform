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

import net.firejack.platform.core.utils.OpenFlameSpringContext;
import net.firejack.platform.core.validation.annotation.Condition;
import net.firejack.platform.core.validation.condition.AbstractCondition;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Component("editableValueProcessor")
public class EditableValueProcessor {

    private static final Logger logger = Logger.getLogger(DefaultValueProcessor.class);

    /**
     * @param method
     * @param params
     * @return
     */
    public Boolean editable(Method method, Map<String, String> params) {
        Boolean defaultValue = null;
	    Condition condition = method.getAnnotation(Condition.class);
        if (condition != null) {
            AbstractCondition voCondition = OpenFlameSpringContext.getBean(condition.value());
            defaultValue = voCondition.editable(params);
        }
        return defaultValue;
    }

}
