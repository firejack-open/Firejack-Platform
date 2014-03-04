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