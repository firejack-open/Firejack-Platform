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
