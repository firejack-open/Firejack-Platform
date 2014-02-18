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

import net.firejack.platform.core.validation.IMessageRuleProcessor;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component("ruleProcessor")
public class RuleProcessor {

    private static final Logger logger = Logger.getLogger(RuleProcessor.class);

    @Autowired
    private List<IMessageRuleProcessor> validateProcessors;
//    @Autowired
//    private ValidateNestedProcessor validateNestedProcessor;

//    /**
//     * @param validateProcessors
//     */
//    public void setValidateProcessors(List<IMessageRuleProcessor> validateProcessors) {
//        this.validateProcessors = validateProcessors;
//    }
//
//    /**
//     * @param additionalValidateProcessors
//     */
//    public void setAdditionalValidateProcessors(List<IMessageRuleProcessor> additionalValidateProcessors) {
//        this.additionalValidateProcessors = additionalValidateProcessors;
//    }

    /**
     * @param method
     * @param propertyName
     * @param params
     * @return
     */
    public List<Constraint> generateConstraints(Method method, String propertyName, Map<String, String> params) {
        if (validateProcessors == null) {
            logger.error("ValidateProcessors should be initialized.");
            throw new WebApplicationException();
        }

        List<Constraint> constraints = new ArrayList<Constraint>();
        for (IMessageRuleProcessor validateProcessor : validateProcessors) {
            List<Constraint> constraintList = validateProcessor.generate(method, "'" + propertyName + "'", params);
            if (constraintList != null) {
                constraints.addAll(constraintList);
            }
        }
//        if (validateNestedProcessor != null) {
////            for (IMessageRuleProcessor additionalValidateProcessor : additionalValidateProcessors) {
//                List<Constraint> constraintList = validateNestedProcessor.generate(method, propertyName, params);
//                if (constraintList != null) {
//                    constraints.addAll(constraintList);
//                }
////            }
//        }
        return constraints;
    }

}