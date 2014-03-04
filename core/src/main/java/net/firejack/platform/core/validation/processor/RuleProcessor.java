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