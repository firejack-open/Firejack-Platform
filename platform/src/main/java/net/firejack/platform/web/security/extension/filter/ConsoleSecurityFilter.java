/*
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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