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

package net.firejack.platform.web.security.extension.filter;

import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.EnvHandler;
import net.firejack.platform.web.security.extension.filter.handlers.ConsoleDefaultPageHandler;
import net.firejack.platform.web.security.filter.OpenFlameFilter;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.permission.IPermissionContainerRule;
import net.firejack.platform.web.security.resource.IResourceLocationContainerRule;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.List;

/**
 * Extension for OpenFlame Security Filter adapted for OpenFlame Console.
 */
public class ConsoleSecurityFilter extends OpenFlameFilter implements IDefaultTargetUrlProvider, EnvHandler {
    private static final Logger logger = Logger.getLogger(ConsoleSecurityFilter.class);

    public final static String PARAM_INSTALLATION_URL = "installation-url";

    public static final String BEAN_NAME_PERMISSION_CONTAINER_RULES = "permissionContainerRules";
    public static final String BEAN_NAME_RESOURCE_LOCATION_CONTAINER_RULES = "resourceLocationContainerRules";

    private WebApplicationContext applicationContext;

    /**
     * Initialize filter
     * @param filterConfig filter configuration
     * @throws ServletException throws Servlet Exception
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        super.init(filterConfig);
        OpenFlameSecurityConstants.setClientContext(false);
        OpenFlameSecurityConstants.setBaseUrl(Env.FIREJACK_URL.getValue());
        setAuthenticationSuccessHandler(new ConsoleDefaultPageHandler(this));
	    Env.FIREJACK_URL.addHandler(this);
    }

	@Override
	public void change(Env key, String value) {
		OpenFlameSecurityConstants.setBaseUrl(value);
	}

	/**
     *
     * @see net.firejack.platform.web.security.extension.filter.IDefaultTargetUrlProvider#getDefaultTargetUrl()
     */
    @Override
    public String getDefaultTargetUrl() {
        if (ConfigContainer.isAppInstalled()) {
            return super.getDefaultTargetUrl();
        }
        return getSParameter(PARAM_INSTALLATION_URL);
    }

    /**
     * Get names of required filter parameters
     * @return required filter parameters
     */
    @Override
    protected String[] getRequiredParameterNames() {
        String[] requiredParameterNamesFromSuper = super.getRequiredParameterNames();
        return append(requiredParameterNamesFromSuper, PARAM_INSTALLATION_URL);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<IPermissionContainerRule> populatePermissionContainerRules() {
        List<IPermissionContainerRule> rules;
        try {
            rules = (List<IPermissionContainerRule>)
                    applicationContext.getBean(BEAN_NAME_PERMISSION_CONTAINER_RULES);
        } catch (BeansException e) {
            logger.error(e.getMessage(), e);
            rules = null;
        }
        return rules;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<IResourceLocationContainerRule> populateResourceLocationRules() {
        List<IResourceLocationContainerRule> rules;
        try {
            rules = (List<IResourceLocationContainerRule>)
                    applicationContext.getBean(BEAN_NAME_RESOURCE_LOCATION_CONTAINER_RULES);
        } catch (BeansException e) {
            logger.error(e.getMessage(), e);
            rules = null;
        }
        return rules;
    }

}