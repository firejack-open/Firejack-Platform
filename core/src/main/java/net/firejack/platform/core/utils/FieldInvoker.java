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

package net.firejack.platform.core.utils;

import net.sf.cglib.proxy.Dispatcher;
import net.sf.cglib.proxy.Enhancer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FieldInvoker {
    private static final Logger logger = Logger.getLogger(FieldInvoker.class);
    private Map<Field, List<Class>> targets = new HashMap<Field, List<Class>>();

    /**
     * @param target
     * @param field
     */
    public void add(Class target, Field field) {
        field.setAccessible(true);

        List<Class> classes = targets.get(field);
        if (classes == null) {
            classes = new ArrayList<Class>();
            targets.put(field, classes);
        }
        if (!classes.contains(target)) {
            classes.add(target);
        }
    }

    /**
     * @param factory
     * @param value
     */
    public void invoke(ConfigurableListableBeanFactory factory, String value) {
        for (Map.Entry<Field, List<Class>> entry : targets.entrySet()) {
            Field field = entry.getKey();
            for (Class clazz : entry.getValue()) {
                Map map = factory.getBeansOfType(clazz);
                for (Object target : map.values()) {
                    try {
                        Object convert = convert(field.getType(), value);
                        if (Enhancer.isEnhanced(target.getClass())) {
                            Object source = getCGLIBSource(target, clazz);
                            field.set(source, convert);
                        } else {
                            field.set(target, convert);
                        }
                    } catch (IllegalAccessException e) {
                        logger.warn("Can't set value: " + value + "in field: " + field.getName() + "because of: " + e.getMessage());
                    }
                }
            }
        }
    }

    private Object convert(Class clazz, String value) throws IllegalAccessException {
        if (clazz.equals(String.class)) {
            return value;
        } else if (clazz.equals(Integer.class)) {
            return Integer.valueOf(value);
        } else if (clazz.equals(Long.class)) {
            return Long.valueOf(value);
        } else if (clazz.equals(Double.class)) {
            return Double.valueOf(value);
        } else if (clazz.equals(Boolean.class)) {
            return Boolean.valueOf(value);
        } else {
            throw new IllegalAccessException("Not supported type:" + clazz.getSimpleName());
        }
    }

    private static Object getCGLIBSource(Object target, Class clazz) {
        for (int i = 0; true; ++i) {
            try {
                Object callback = ClassUtils.getProperty(target, "CGLIB$CALLBACK_" + i);
                if (callback instanceof Dispatcher) {
                    Object source = ((Dispatcher) callback).loadObject();
                    if (source.getClass().equals(clazz)) {
                        return source;
                    }
                }
            } catch (NoSuchFieldException e) {
                break;
            } catch (Exception e) {
	            //ignore
            }
        }
        return null;
    }
}
