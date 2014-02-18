package net.firejack.platform.model.config.placeholder;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */


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
