package net.firejack.platform.model.config.listener;

import com.sun.xml.bind.v2.ClassFactory;
import net.firejack.platform.core.utils.ClassUtils;
import net.firejack.platform.model.config.GatewayListener;
import net.firejack.platform.model.config.GatewayLoader;
import net.firejack.platform.model.config.hibernate.HibernateFactoryBean;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.cxf.jaxb.JAXBContextCache;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import java.lang.reflect.Field;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

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

public class ConfigContextLoaderListener extends ContextLoaderListener implements GatewayListener {

    private ServletContextEvent event;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        this.event = event;
        GatewayLoader.getInstance().addListener(this);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        try {
            Map<String, BasicDataSource> dataSources = getCurrentWebApplicationContext().getBeansOfType(BasicDataSource.class);
            for (BasicDataSource dataSource : dataSources.values()) {
                try {
                    dataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                try {
                    DriverManager.deregisterDriver(drivers.nextElement());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            JAXBContextCache.clearCaches();
            ClassFactory.cleanCache();
            CachedIntrospectionResults.clearClassLoader(getClass().getClassLoader());
        } finally {
            super.contextDestroyed(event);
        }
    }

    @Override
    public void start() {
        super.contextInitialized(event);

        ConfigurableApplicationContext context = (ConfigurableApplicationContext) getCurrentWebApplicationContext();

        AnnotationTransactionAttributeSource source = context.getBean(AnnotationTransactionAttributeSource.class);
        Map<Object, DefaultTransactionAttribute> map = ClassUtils.getProperty(source, "attributeCache");

        Map<String, BeanDefinition> managers = context.getBean(HibernateFactoryBean.HIBERNATE_MANAGER, Map.class);
        for (Map.Entry<String, BeanDefinition> entry : managers.entrySet()) {
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
            beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
        }

        Map<Class, String> targets = context.getBean(HibernateFactoryBean.HIBERNATE_TARGET, Map.class);
        Field targetField = null;

        for (Map.Entry<Object, DefaultTransactionAttribute> entry : map.entrySet()) {
            Object key = entry.getKey();
            if (targetField == null)
                targetField = ClassUtils.getField(key.getClass(), "targetClass");

            Class targetClass = ClassUtils.getProperty(key, targetField);
            String name = targets.get(targetClass);
            if (name != null)
                entry.getValue().setQualifier(name);
        }
    }
}
