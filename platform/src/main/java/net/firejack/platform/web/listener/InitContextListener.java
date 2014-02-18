/**
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.web.listener;

import com.sun.xml.bind.v2.ClassFactory;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.utils.OpenFlameConfig;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.x509.KeyUtils;
import org.apache.cxf.jaxb.JAXBContextCache;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.Map;


public class InitContextListener extends ContextLoaderListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
	    File app = Env.getDefaultEnvFile();
	    File keyStore = InstallUtils.getKeyStore();

	    Map<String, String> propertiesMap = KeyUtils.getMapProperties(app, keyStore);
	    ConfigContainer.putAll(propertiesMap);

	    File xmlProp = InstallUtils.getXmlEnv();
        if (!ConfigContainer.isAppInstalled() && xmlProp.exists()) {
            Map<String, String> settings = EnvironmentsUtils.convertFromXml(xmlProp);
            ConfigContainer.putAll(settings);
        }

        File propFile = InstallUtils.getPropEnv();
        if (!ConfigContainer.isAppInstalled() && propFile.exists()) {
            ConfigContainer.putAll(MiscUtils.getProperties(propFile));
        }

	    if (!xmlProp.exists()) {
		    if (!OpenFlameConfig.DOMAIN_URL.exist())
			    OpenFlameConfig.DOMAIN_URL.setValue(TomcatUtils.getCatalinaHost(sce));
		    if (!OpenFlameConfig.PORT.exist())
	            OpenFlameConfig.PORT.setValue(TomcatUtils.getCatalinaPort(sce).toString());
            if (!OpenFlameConfig.CONTEXT_URL.exist())
	            OpenFlameConfig.CONTEXT_URL.setValue(TomcatUtils.getCatalinaContext(sce));
        }

	    String domainUrl = OpenFlameConfig.DOMAIN_URL.getValue();
	    String port = OpenFlameConfig.PORT.getValue();
	    String contextUrl = OpenFlameConfig.CONTEXT_URL.getValue();
	    if (domainUrl != null && port != null && contextUrl != null) {
		    Env.FIREJACK_URL.setValue(WebUtils.getNormalizedUrl(domainUrl, port, contextUrl));
	    }

	    ConfigContainer.setHost(TomcatUtils.getCatalinaHost(sce));
	    ConfigContainer.setPort(TomcatUtils.getCatalinaPort(sce));

        super.contextInitialized(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
	    try {
		    OpenFlameDataSource bean = OpenFlameSpringContext.getBean(OpenFlameDataSource.class);
		    bean.close();
	    } catch (SQLException e) {
		    e.printStackTrace();
	    }
	    try {
		    Scheduler scheduler = OpenFlameSpringContext.getBean(Scheduler.class);
		    scheduler.shutdown();
	    } catch (SchedulerException e) {
		    e.printStackTrace();
	    }
	    Env.clean();
	    JAXBContextCache.clearCaches();
	    ClassFactory.cleanCache();
	    CachedIntrospectionResults.clearClassLoader(getClass().getClassLoader());

        super.contextDestroyed(sce);
    }
}
