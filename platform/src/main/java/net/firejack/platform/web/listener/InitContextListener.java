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
