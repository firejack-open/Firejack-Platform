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

package net.firejack.platform.core.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OpenFlameSpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		 context = applicationContext;
	}

    /**
     * @return
     */
    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * @param beanName
     * @return
     */
    public static <T> T getBean(String beanName) {
	    if (context == null) throw new NoSuchBeanDefinitionException("Context not initialized");
	    return (T) context.getBean(beanName);


    }

	/**
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

	/**
     * @param clazz
     * @return
     */
    public static <T> Map<String, T> getBeans(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    public static <T> List<T> getBeansByClass(Class<T> clazz) {
        return new ArrayList<T>(context.getBeansOfType(clazz).values());
    }

	public static void addSingleton(String name, Object bean) {
		ConfigurableListableBeanFactory factory = ((ConfigurableApplicationContext) context).getBeanFactory();
		if (!factory.containsSingleton(name)) {
			factory.registerSingleton(name, bean);
		}
	}
}
