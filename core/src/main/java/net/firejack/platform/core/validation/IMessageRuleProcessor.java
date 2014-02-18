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

import net.firejack.platform.core.validation.annotation.ValidationMode;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import net.firejack.platform.core.validation.exception.RuleValidationException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;


public interface IMessageRuleProcessor {

    /**
     *
     * @param readMethod
     * @param property
     * @param value
     * @param mode
     * @return
     * @throws RuleValidationException
     *
     */
    List<ValidationMessage> validate(Method readMethod, String property, Object value, ValidationMode mode) throws RuleValidationException;

    /**
     * @param readMethod
     * @param property
     * @param params
     * @return
     */
    List<Constraint> generate(Method readMethod, String property, Map<String, String> params);

}