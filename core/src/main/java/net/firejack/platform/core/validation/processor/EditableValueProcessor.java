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
