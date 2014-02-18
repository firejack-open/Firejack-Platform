package net.firejack.platform.model.config.hibernate;
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

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.AtomikosSQLException;
import net.firejack.platform.core.model.BaseEntityModel;
import net.firejack.platform.core.model.registry.DatabaseName;
import net.firejack.platform.core.store.HibernateSupport;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.StringUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.util.*;

public class HibernateFactoryBean implements ApplicationContextAware, InitializingBean {
    private static final Logger logger = Logger.getLogger(HibernateFactoryBean.class);

    public static final String HIBERNATE_MANAGER = "hibernateManager";
    public static final String HIBERNATE_TARGET = "hibernateTarget";
    public static final String HIBERNATE_DEFAULT_PREFIX = "default";
    public static final String HIBERNATE_TEMPLATE_PREFIX = "template";
    public static final String XA = "XA";
    public static final String HIBERNATE_DATA_SOURCE_SUFFIX = "DataSource";
    public static final String HIBERNATE_PROPERTIES_SUFFIX = "HibernateProperties";
    public static final String HIBERNATE_SESSION_FACTORY_SUFFIX = "SessionFactory";
    public static final String HIBERNATE_TRANSACTION_MANAGER_SUFFIX = "TransactionManager";

    public static final String TRANSACTION_STRATEGY = "com.atomikos.icatch.jta.hibernate3.AtomikosJTATransactionFactory";
    public static final String TRANSACTION_MANAGER_STRATEGY = "com.atomikos.icatch.jta.hibernate3.TransactionManagerLookup";

    private Map<String, BeanDefinition> registers = new HashMap<String, BeanDefinition>();
    private Map<Class, String> targets = new HashMap<Class, String>();
    private Map<String, Database> databases = new HashMap<String, Database>();
    private Map<String, Database> xaDatabases = new HashMap<String, Database>();
    private List<DisposableBean> disposableBeans = new ArrayList<DisposableBean>();
    private ConfigurableApplicationContext context;
    private ClassPathXmlApplicationContext defaultHibernate;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = (ConfigurableApplicationContext) context;
        defaultHibernate = new ClassPathXmlApplicationContext("hibernate-default.xml");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        context.getBeanFactory().registerSingleton(HIBERNATE_MANAGER, registers);
        context.getBeanFactory().registerSingleton(HIBERNATE_TARGET, targets);

        findDatabase();

        buildHibernateTransactionManager();
        buildXAHibernateTransactionManager();
        defaultHibernate.destroy();
    }

    private void findDatabase() {
        List<String> xaDomains = new ArrayList<String>();

        String xa = ConfigContainer.get("xa");
        if (StringUtils.isNotBlank(xa)) {
            Collections.addAll(xaDomains, xa.split(";"));
        }

        Properties properties = ConfigContainer.getProperties();

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            String[] strings = key.split("\\.");
            if (strings.length == 3 && strings[0].equals("db")) {
                Database database = databases.get(strings[1]);
                if (database == null) {
                    database = new Database();
                    databases.put(strings[1], database);
                }
                if (strings[2].equals("type")) {
                    database.setType(DatabaseName.valueOf(value));
                } else if (strings[2].equals("domains")) {
                    List<String> domains = Arrays.asList(value.split(";"));
                    database.setDomains(domains);
                    for (String domain : domains) {
                        if (domain.split("\\.").length == 3)
                            database.setRoot(true);
                    }
                } else if (strings[2].equals("url")) {
                    database.setUrl(value);
                } else if (strings[2].equals("username")) {
                    database.setUsername(value);
                } else if (strings[2].equals("password")) {
                    database.setPassword(value);
                }
            }
        }

        Map<String, HibernateSupport> stories = context.getBeansOfType(HibernateSupport.class);

        for (Database database : databases.values()) {
            if (database.isRoot())
                database.setStores(stories.values());
            for (Iterator<Map.Entry<String, HibernateSupport>> iterator = stories.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, HibernateSupport> supportEntry = iterator.next();
                HibernateSupport support = supportEntry.getValue();
                if (database.addStore(support)) {
                    iterator.remove();
                }
            }
        }

        if (xaDomains.size() > 1) {
            for (Iterator<Map.Entry<String, Database>> iterator = databases.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Database> entry = iterator.next();
                List<String> domains = new ArrayList<String>(entry.getValue().getDomains());

                for (Iterator<String> stringIterator = domains.iterator(); stringIterator.hasNext(); ) {
                    if (xaDomains.contains(stringIterator.next())) {
                        xaDatabases.put(entry.getKey(), entry.getValue());
                        stringIterator.remove();
                    }
                }

                if (domains.isEmpty())
                    iterator.remove();
            }
        }
    }

    private void buildHibernateTransactionManager() {
        for (Map.Entry<String, Database> entry : databases.entrySet()) {
            String name = entry.getKey();
            Database database = entry.getValue();

            BasicDataSource dataSource = null;
            try {
                dataSource = context.getBean(name + HIBERNATE_DATA_SOURCE_SUFFIX, BasicDataSource.class);
            } catch (BeansException e) {
                logger.info("Manual Hibernate DataSource: " + name + HIBERNATE_DATA_SOURCE_SUFFIX + " not found use default");
            }

            if (dataSource == null) {
                dataSource = defaultHibernate.getBean(HIBERNATE_DEFAULT_PREFIX + HIBERNATE_DATA_SOURCE_SUFFIX, BasicDataSource.class);
                dataSource.setDriverClassName(database.type.getDriver());
                dataSource.setUrl(database.getUrl());
                dataSource.setUsername(database.getUsername());
                dataSource.setPassword(database.getPassword());
                dataSource.setValidationQuery(database.getType().getValidate());

                context.getBeanFactory().registerSingleton(name + HIBERNATE_DATA_SOURCE_SUFFIX, dataSource);
            }

            Properties properties = null;
            try {
                properties = context.getBean(name + HIBERNATE_PROPERTIES_SUFFIX, Properties.class);
            } catch (BeansException e) {
                logger.info("Manual Hibernate properties: " + name + HIBERNATE_PROPERTIES_SUFFIX + " not found use default");
            }

            if (properties == null) {
                properties = defaultHibernate.getBean(HIBERNATE_DEFAULT_PREFIX + HIBERNATE_PROPERTIES_SUFFIX, Properties.class);
                properties.put(Environment.DIALECT, database.getType().getDialect());

                context.getBeanFactory().registerSingleton(name + HIBERNATE_PROPERTIES_SUFFIX, properties);
            }

            SessionFactory sessionFactory = null;
            try {
                sessionFactory = context.getBean(name + HIBERNATE_SESSION_FACTORY_SUFFIX, SessionFactory.class);
            } catch (BeansException e) {
                logger.info("Manual Hibernate Session Factory: " + name + HIBERNATE_SESSION_FACTORY_SUFFIX + " not found use default");
            }

            if (sessionFactory == null) {
                try {
                    AnnotationSessionFactoryBean annotationSessionFactoryBean = new AnnotationSessionFactoryBean();
                    annotationSessionFactoryBean.setAnnotatedClasses(new Class[]{BaseEntityModel.class});
                    annotationSessionFactoryBean.setDataSource(dataSource);
                    annotationSessionFactoryBean.setHibernateProperties(properties);
                    annotationSessionFactoryBean.setPackagesToScan(database.getScanPackages());
                    annotationSessionFactoryBean.setNamingStrategy(new OpenFlameNamingStrategy(database.getType()));

                    if (database.getType() == DatabaseName.MySQL || database.getType() == DatabaseName.MSSQL) {
                        annotationSessionFactoryBean.setLobHandler(new DefaultLobHandler());
                    } else if (database.getType() == DatabaseName.Oracle) {
                        annotationSessionFactoryBean.setLobHandler(new OracleLobHandler());
                    }

                    disposableBeans.add(annotationSessionFactoryBean);
                    annotationSessionFactoryBean.afterPropertiesSet();
                    sessionFactory = annotationSessionFactoryBean.getObject();
                } catch (Exception e) {
                    logger.error(e, e);
                }
            }

            HibernateTemplate template = new HibernateTemplate();
            template.setSessionFactory(sessionFactory);

            context.getBeanFactory().registerSingleton(name + HIBERNATE_TEMPLATE_PREFIX, template);
            database.setHibernateSupport(template);
            logger.info("Initialize Hibernate support for stories: " + database);

            String beanName = name + HIBERNATE_TRANSACTION_MANAGER_SUFFIX;

            ConstructorArgumentValues values = new ConstructorArgumentValues();
            values.addGenericArgumentValue(sessionFactory);

            RootBeanDefinition definition = new RootBeanDefinition(HibernateTransactionManager.class);
            definition.setConstructorArgumentValues(values);
            registers.put(beanName, definition);

            Collection<HibernateSupport> stores = database.getStores();
            for (HibernateSupport store : stores) {
                targets.put(((Advised) store).getTargetClass(), beanName);
            }
        }
    }

    private void buildXAHibernateTransactionManager() {
        if (xaDatabases.size() == 0)
            return;

        String beanName = XA + HIBERNATE_TRANSACTION_MANAGER_SUFFIX;

        for (Map.Entry<String, Database> entry : xaDatabases.entrySet()) {
            String name = entry.getKey();
            Database database = entry.getValue();

            AtomikosDataSourceBean dataSource = null;
            try {
                dataSource = context.getBean(name + HIBERNATE_DATA_SOURCE_SUFFIX, AtomikosDataSourceBean.class);
            } catch (BeansException e) {
                logger.info("Manual XA Hibernate DataSource: " + name + HIBERNATE_DATA_SOURCE_SUFFIX + " not found use default");
            }

            if (dataSource == null) {
                try {
                    dataSource = defaultHibernate.getBean(HIBERNATE_DEFAULT_PREFIX + XA + HIBERNATE_DATA_SOURCE_SUFFIX, AtomikosDataSourceBean.class);
                    dataSource.setUniqueResourceName(name);
                    dataSource.setXaDataSourceClassName(database.getType().getXads());
                    dataSource.setTestQuery(database.getType().getValidate());
                    Properties xaProperties = new Properties();
                    xaProperties.put("URL", database.getUrl());
                    xaProperties.put("user", database.getUsername());
                    xaProperties.put("password", database.getPassword());
                    if (database.getType() == DatabaseName.MySQL) {
                        xaProperties.put("pinGlobalTxToPhysicalConnection", true);
                    }
                    dataSource.setXaProperties(xaProperties);
                    dataSource.init();

                    context.getBeanFactory().registerSingleton(name + HIBERNATE_DATA_SOURCE_SUFFIX, dataSource);
                } catch (AtomikosSQLException e) {
                    logger.error(e, e);
                }
            }

            Properties properties = null;
            try {
                properties = context.getBean(name + HIBERNATE_PROPERTIES_SUFFIX, Properties.class);
            } catch (BeansException e) {
                logger.info("Manual XA Hibernate properties: " + name + HIBERNATE_PROPERTIES_SUFFIX + " not found use default");
            }

            if (properties == null) {
                properties = defaultHibernate.getBean(HIBERNATE_DEFAULT_PREFIX + HIBERNATE_PROPERTIES_SUFFIX, Properties.class);
                properties.put(Environment.DIALECT, database.getType().getDialect());
                properties.put(Environment.TRANSACTION_STRATEGY, TRANSACTION_STRATEGY);
                properties.put(Environment.TRANSACTION_MANAGER_STRATEGY, TRANSACTION_MANAGER_STRATEGY);

                context.getBeanFactory().registerSingleton(name + HIBERNATE_PROPERTIES_SUFFIX, properties);
            }

            SessionFactory sessionFactory = null;
            try {
                sessionFactory = context.getBean(name + HIBERNATE_SESSION_FACTORY_SUFFIX, SessionFactory.class);
            } catch (BeansException e) {
                logger.info("Manual XA Hibernate Session Factory: " + name + HIBERNATE_SESSION_FACTORY_SUFFIX + " not found use default");
            }

            if (sessionFactory == null) {
                try {
                    AnnotationSessionFactoryBean annotationSessionFactoryBean = new AnnotationSessionFactoryBean();
                    annotationSessionFactoryBean.setAnnotatedClasses(new Class[]{BaseEntityModel.class});
                    annotationSessionFactoryBean.setDataSource(dataSource);
                    annotationSessionFactoryBean.setHibernateProperties(properties);
                    annotationSessionFactoryBean.setPackagesToScan(database.getScanPackages());
                    annotationSessionFactoryBean.setNamingStrategy(new OpenFlameNamingStrategy(database.getType()));

                    if (database.getType() == DatabaseName.MySQL || database.getType() == DatabaseName.MSSQL) {
                        annotationSessionFactoryBean.setLobHandler(new DefaultLobHandler());
                    } else if (database.getType() == DatabaseName.Oracle) {
                        annotationSessionFactoryBean.setLobHandler(new OracleLobHandler());
                    }

                    disposableBeans.add(annotationSessionFactoryBean);
                    annotationSessionFactoryBean.afterPropertiesSet();
                    sessionFactory = annotationSessionFactoryBean.getObject();
                } catch (Exception e) {
                    logger.error(e, e);
                }

                Collection<HibernateSupport> stores = database.getStores();
                for (HibernateSupport store : stores) {
                    targets.put(((Advised) store).getTargetClass(), beanName);
                }
            }

            HibernateTemplate template = new HibernateTemplate();
            template.setSessionFactory(sessionFactory);

            context.getBeanFactory().registerSingleton(HIBERNATE_TEMPLATE_PREFIX + name, template);
            database.setHibernateSupport(template);
            logger.info("Initialize Hibernate :" + HIBERNATE_TEMPLATE_PREFIX + name + " for stories:" + database);
        }

        ConstructorArgumentValues values = new ConstructorArgumentValues();
        values.addGenericArgumentValue(new UserTransactionImp());
        values.addGenericArgumentValue(new UserTransactionManager());

        RootBeanDefinition definition = new RootBeanDefinition(JtaTransactionManager.class);
        definition.setConstructorArgumentValues(values);
        registers.put(beanName, definition);
    }

    public void destroy() {
        for (String name : databases.keySet()) {
            try {
                BasicDataSource dataSource = context.getBean(name + HIBERNATE_DATA_SOURCE_SUFFIX, BasicDataSource.class);
                dataSource.close();
            } catch (Exception e) {
                logger.error(e);
            }
        }

        for (String name : xaDatabases.keySet()) {
            try {
                AtomikosDataSourceBean dataSource = context.getBean(name + HIBERNATE_DATA_SOURCE_SUFFIX, AtomikosDataSourceBean.class);
                dataSource.close();
            } catch (Exception e) {
                logger.error(e);
            }
        }

        for (DisposableBean bean : disposableBeans) {
            try {
                bean.destroy();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private class Database {
        private DatabaseName type;
        private boolean root;
        private List<String> domains;
        private String url;
        private String username;
        private String password;
        private Collection<HibernateSupport> stores = new ArrayList<HibernateSupport>();

        public DatabaseName getType() {
            return type;
        }

        public void setType(DatabaseName type) {
            this.type = type;
        }

        private boolean isRoot() {
            return root;
        }

        private void setRoot(boolean root) {
            this.root = root;
        }

        public List<String> getDomains() {
            return domains;
        }

        public void setDomains(List<String> domains) {
            this.domains = domains;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Collection<HibernateSupport> getStores() {
            return stores;
        }

        public void setStores(Collection<HibernateSupport> stores) {
            this.stores = stores;
        }

        public boolean addStore(HibernateSupport store) {
            if (isRoot()) return false;
            String name = store.getClass().getPackage().getName().replaceAll("(.*?\\.)[\\w\\-]+$", "$1");
            for (String domain : domains) {
                if (name.startsWith(domain + ".")) {
                    return this.stores.add(store);
                }
            }
            return false;
        }

        public String[] getScanPackages() {
            String[] strings = new String[stores.size()];
            int index = 0;
            for (HibernateSupport store : stores) {
                strings[index++] = store.getClass().getPackage().getName().replaceAll("(.*?)\\.[\\w\\-]+$", "$1");
            }
            return strings;
        }

        public void setHibernateSupport(HibernateTemplate template) {
            for (HibernateSupport store : stores) {
                store.setTemplate(template);
            }
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            for (HibernateSupport store : stores) {
                sb.append(store.getClass().getSimpleName()).append(" ");
            }
            return sb.toString();
        }
    }
}