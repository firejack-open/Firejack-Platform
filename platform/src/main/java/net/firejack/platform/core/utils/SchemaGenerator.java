/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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