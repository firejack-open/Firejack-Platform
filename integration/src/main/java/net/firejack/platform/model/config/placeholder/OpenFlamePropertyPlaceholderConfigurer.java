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

package net.firejack.platform.model.config.placeholder;

import net.firejack.platform.core.utils.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class OpenFlamePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements ConfigContextListener {

    private Map<String, FieldInvoker> invokers = new HashMap<String, FieldInvoker>();
    private Properties properties;
    private ConfigurableListableBeanFactory factory;
    private boolean runtimeOverride = false;

    /**
     * @param runtimeOverride
     */
    public void setRuntimeOverride(boolean runtimeOverride) {
        this.runtimeOverride = runtimeOverride;
    }

	public void destroy(){
		invokers.clear();
	}

    @Override
    protected void processProperties(ConfigurableListableBeanFactory factory, Properties props) throws BeansException {
        ConfigContainer.merge(props);
        analyze(props);

        if (runtimeOverride) {
            researchPlaceHolder(factory);
            ConfigContainer.addListener(this);
        }

        super.processProperties(factory, props);
    }

    public void put(String key, String value) {
        properties.put(key, value);
        FieldInvoker invoker = invokers.get(key);
        if (invoker != null) {
            invoker.invoke(factory, value);
        }
    }

	private void analyze(Properties map) {
		String temp = FileUtils.getTempDirectoryPath();
		for (Map.Entry<Object, Object> entry : map.entrySet()) {
			if (entry.getValue() instanceof String) {
				String key = (String) entry.getKey();
				File file = new File((String) entry.getValue());
				try {
					if (key.matches("^.*?(dir|path|file|folder).*?$") && !canWritePath(file)) {
						String value = temp + File.separator + file.getName();
						logger.info("You have defined path for" + key + " is not writable. Path has been changed from" + entry.getValue() + "  to new " + value);
						map.put(key, value);
					}
				} catch (IllegalArgumentException e) {
					logger.trace(e.getMessage());
				}
			}
		}
		this.properties = map;
	}

	private boolean canWritePath(File file) {
        if (file == null || file.getPath().equals(File.separator))
            throw new IllegalArgumentException("Path is not specified");
        if (file.exists()) {
            return file.canWrite();
        } else {
            return canWritePath(file.getParentFile());
        }
    }

    private void researchPlaceHolder(ConfigurableListableBeanFactory factory) {
        String[] names = factory.getBeanDefinitionNames();
        for (String name : names) {
            try {
                BeanDefinition definition = factory.getBeanDefinition(name);
                String className = definition.getBeanClassName();
                Class<?> clazz = Class.forName(className);
                //Annotations
                findFieldAnnotations(clazz, clazz);

                //Properties
                List<PropertyValue> values = definition.getPropertyValues().getPropertyValueList();
                if (!values.isEmpty()) {
                    for (PropertyValue value : values) {
                        if (value.getValue() instanceof TypedStringValue) {
                            String key = ((TypedStringValue) value.getValue()).getValue();
                            if (key.matches("^\\$\\{.*\\}$")) {
                                put(clazz, ClassUtils.getField(clazz, value.getName()), key);
                            }
                        }
                    }
                }

            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage());
            }
        }
        this.factory = factory;
    }

    private void put(Class<?> clazz, Field field, String key) {
        if (field != null) {
            key = key.replaceAll("\\$|\\{|\\}", "");
            FieldInvoker invoker = invokers.get(key);
            if (invoker == null) {
                invoker = new FieldInvoker();
                invokers.put(key, invoker);
            }
            invoker.add(clazz, field);
        }
    }

    private void findFieldAnnotations(Class<?> parent, Class<?> clazz) {
        if (!clazz.equals(Object.class)) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Value value = field.getAnnotation(Value.class);
                if (value != null) {
                    put(parent, field, value.value());
                }
            }
            findFieldAnnotations(parent, clazz.getSuperclass());
        }
    }

}
