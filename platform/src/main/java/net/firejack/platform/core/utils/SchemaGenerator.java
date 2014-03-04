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

import org.apache.log4j.Logger;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;


public final class SchemaGenerator {

    private static final Logger logger = Logger.getLogger(SchemaGenerator.class);

    /**
     * Generates database schema from given application config resources
     *
     * @param configResources  - list of application config resources with mapped hibernates entities
     * @param propertyFileName - placeholder property file
     * @param outputFileName   - output sql schema file name
     */
    public void generateSchema(String[] configResources, String propertyFileName, String outputFileName) {
        generateSchema(configResources, propertyFileName, outputFileName, "sessionFactory");
    }

    /**
     * Generates database schema from given application config resources
     *
     * @param configResources        - list of application config resources with mapped hibernates entities
     * @param propertyFileName       - placeholder property file
     * @param outputFileName         - output sql schema file name
     * @param sessionFactoryBeanName - spring session factory bean name
     */
    public void generateSchema(String[] configResources, String propertyFileName, String outputFileName, String sessionFactoryBeanName) {
        AbstractApplicationContext appContext = new ClassPathXmlApplicationContext(configResources, false);
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Resource res = new ClassPathResource("/" + propertyFileName);
        ppc.setLocation(res);
        appContext.addBeanFactoryPostProcessor(ppc);
        appContext.refresh();
        LocalSessionFactoryBean sessionFactory;
        try {
            sessionFactory = appContext.getBean("&" + sessionFactoryBeanName, LocalSessionFactoryBean.class);
        } catch (NoSuchBeanDefinitionException e) {
            logger.error("Couldn't load hibernate session factory configuration bean", e);
            return;
        }
        SchemaExport schemaExport = new SchemaExport(sessionFactory.getConfiguration());
        schemaExport.setOutputFile(outputFileName).setDelimiter(";");
        schemaExport.execute(false, false, false, false);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        schemaGenerator.generateSchema(
                new String[]{
                        "/spring/hibernate-config.xml",
                        "/spring/domain-config.xml"
                },
                args[0],
                "squiqitInitial.sql"
        );
    }

}