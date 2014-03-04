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

package net.firejack.platform.core.validation.constraint;

import net.firejack.platform.core.utils.ContextRefreshListener;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class RuleMapper extends ContextRefreshListener {

    private static Map<String, Class<?>> CONSTRAINT_LOCATIONS = new HashMap<String, Class<?>>();
    private static final Logger logger = Logger.getLogger(RuleMapper.class);


    protected void onContextRefreshed(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, Object> beans =  context.getBeansWithAnnotation(RuleSource.class);

        for (Object bean : beans.values()) {
            Class<?> clazz = bean.getClass();
            RuleSource ruleSource = clazz.getAnnotation(RuleSource.class);
            if (ruleSource != null) {
                if (StringUtils.isNotBlank(ruleSource.value())) {
                    CONSTRAINT_LOCATIONS.put(ruleSource.value(), clazz);
                }
            }
        }
    }

    /**
     * @param id
     * @return
     */
    public static ConstraintsSourceClass getConstrainedType(String id) {
        ConstraintsSourceClass sourceClass = null;
        if (StringUtils.isBlank(id)) {
            return sourceClass;
        }
        String[] values = id.split("\\?");
        if (values.length == 1) {
            sourceClass = new ConstraintsSourceClass(CONSTRAINT_LOCATIONS.get(id));
        } else {
            sourceClass = new ConstraintsSourceClass(CONSTRAINT_LOCATIONS.get(values[0]));
            Map<String, String> params = new HashMap<String, String>();
            String[] parameters = values[1].split("\\|");
            for (String parameter : parameters) {
                String[] p = parameter.split("=");
                if (p.length == 2) {
                    params.put(p[0], p[1]);
                } else {
                    params.put(p[0], null);
                }
            }
            sourceClass.setParams(params);
        }
        return sourceClass;
    }
}