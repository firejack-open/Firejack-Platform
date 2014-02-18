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
package net.firejack.platform.model.config.servlet;

import net.firejack.platform.api.authority.domain.UserPermission;
import net.firejack.platform.api.directory.domain.User;
import net.firejack.platform.api.site.domain.NavigationElement;
import net.firejack.platform.core.model.registry.NavigationElementType;
import net.firejack.platform.core.model.user.IUserInfoProvider;
import net.firejack.platform.core.utils.*;
import net.firejack.platform.model.config.GatewayListener;
import net.firejack.platform.model.config.GatewayLoader;
import net.firejack.platform.model.config.servlet.preprocessor.IGatewayPreProcessor;
import net.firejack.platform.utils.WebUtils;
import net.firejack.platform.web.security.InstantSecurity;
import net.firejack.platform.web.security.filter.message.FilterMessage;
import net.firejack.platform.web.security.filter.message.FilterMessageStock;
import net.firejack.platform.web.security.model.OpenFlameSecurityConstants;
import net.firejack.platform.web.security.model.context.ContextManager;
import net.firejack.platform.web.security.model.context.OPFContext;
import net.firejack.platform.web.security.navigation.INavElementContainer;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatewayServlet extends HttpServlet implements GatewayListener {
	private static final long serialVersionUID = 8569156630095203870L;

    protected Logger logger = Logger.getLogger(GatewayServlet.class);

	private transient ServletConfig config;
	private transient VelocityEngine velocity;
	private String path;
	private static final String LOG_DIR = "velocity.log.dir";

    private static String CACHE_VERSION;

    private Map<String, IGatewayPreProcessor> preProcessors;

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		GatewayLoader.getInstance().addListener(this);

		String logDir = config.getInitParameter(LOG_DIR);
		ConfigContainer.put("log.directory", logDir != null ? logDir : "logs");
	}

	@Override
	public void start() {
		try {
			super.init(config);

            CACHE_VERSION = SecurityHelper.generateRandomSequence(6);

			String lookup = OpenFlameSecurityConstants.getPackageLookup();
			if (StringUtils.isNotEmpty(lookup)) {
				this.path = lookup.replaceAll("\\.", "/");
			}

			WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			this.velocity = wac.getBean("velocityEngine", VelocityEngine.class);

            preProcessors = wac.getBeansOfType(IGatewayPreProcessor.class);
        } catch (ServletException e) {
			log(e.getMessage(), e);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getPathInfo().endsWith("reset-host")) {
            String url = WebUtils.getNormalizedUrl(request.getServerName(), request.getLocalPort(), request.getContextPath());
            OpenFlameSecurityConstants.setBaseUrl(url);
            String platform = WebUtils.getNormalizedUrl(request.getServerName(), request.getLocalPort(), "/platform");
            Env.FIREJACK_URL.setValue(platform);
            response.sendRedirect("/home");
        }
        response.setContentType("text/html;charset=UTF-8");
        Map<Object, Object> map = new HashMap<Object, Object>();

        map.put("cacheVersion", CACHE_VERSION);

		INavElementContainer navElementContainer = OPFContext.getContext().getNavElementContainer();
		List<NavigationElement> navigationElements = navElementContainer.getAllowedNavElementList();
		NavigationElement currentNavigationElement = null;
		for (NavigationElement navigationElement : navigationElements) {
            if (navigationElement.getUrlPath() != null && navigationElement.getUrlPath().equals(request.getPathInfo())) {
                if (NavigationElementType.PAGE.equals(navigationElement.getElementType())) {
                    currentNavigationElement = navigationElement;
                } else if (NavigationElementType.WORKFLOW.equals(navigationElement.getElementType())) {
                    currentNavigationElement = navigationElement;
                }
            }
		}
		if (currentNavigationElement != null) {
			map.put("pageUrl", this.path + currentNavigationElement.getPageUrl());
			map.put("navigationLookup", currentNavigationElement.getLookup());
			map.put("navigationName", currentNavigationElement.getName());
		}

        addGlobalParams(request, map);

		if (ContextManager.isUserAuthenticated()) {
			IUserInfoProvider currentUser = ContextManager.getUserInfoProvider();
			map.put("currentUserId", String.valueOf(currentUser.getId()));
			map.put("currentUsername", currentUser.getUsername());
            if (currentUser instanceof User) {
                User user = (User) currentUser;
                map.put("currentUserFirstName", user.getFirstName());
                map.put("currentUserLastName", user.getLastName());
                map.put("currentUserEmail", user.getEmail());
            }
			map.put("isLogged", "true");

			UserPermission editDocumentationPermission = new UserPermission(OpenFlame.DOCUMENTATION_EDIT);
			map.put("canEditResource", String.valueOf(OPFContext.getContext().getPrincipal().checkUserPermission(editDocumentationPermission)));
		} else {
			map.put("currentUserId", "null");
			map.put("currentUsername", "");
			map.put("isLogged", "false");
			map.put("canEditResource", "false");
		}

        Map<String, String> extraParams = new HashMap<String, String>();

        addFilterMessages(extraParams);

        addExtraParams(request, response, map, currentNavigationElement, extraParams);

        addQueryParams(request, map);

        map.put("isDomainAdmin", InstantSecurity.isDomainAdmin());
        VelocityEngineUtils.mergeTemplate(velocity, "templates/servlet.vsl", map, response.getWriter());
	}

    protected void addGlobalParams(HttpServletRequest request, Map<Object, Object> map) {
        map.put("baseUrl", OpenFlameSecurityConstants.getBaseUrl());
        map.put("opfConsoleUrl", Env.FIREJACK_URL.getValue());
        map.put("packageLookup", OpenFlameSecurityConstants.getPackageLookup());
    }

    protected void addFilterMessages(Map<String, String> extraParams) throws IOException {
        List<FilterMessage> filterMessageList = FilterMessageStock.getInstance().getFilterMessages();
        ObjectMapper mapper = new ObjectMapper();
        String filterMessages = mapper.writeValueAsString(filterMessageList);
        extraParams.put("filterMessages", filterMessages);
    }

    protected void addQueryParams(HttpServletRequest request, Map<Object, Object> map) throws IOException {
        boolean isDebugMode = false;
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> queryParams = new HashMap<String, String>();
        Enumeration parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            String[] values = request.getParameterValues(name);
            String param = null;
            if (values.length == 1) {
                param = mapper.writeValueAsString(values[0]);
            } else if (values.length > 1) {
                param = mapper.writeValueAsString(values);
            }
            queryParams.put(name, param);
            if (name.equals("debug")) {
                isDebugMode = "true".equals(values[0]);
            }
        }
        map.put("queryParams", queryParams);
        map.put("isDebugMode", isDebugMode);
    }

    protected void addExtraParams(HttpServletRequest request, HttpServletResponse response, Map<Object, Object> map, NavigationElement currentNavigationElement, Map<String, String> extraParams) {
        for (Map.Entry<String, IGatewayPreProcessor> entry : preProcessors.entrySet()) {
            IGatewayPreProcessor preProcessor = entry.getValue();
            preProcessor.execute(extraParams, request, response, currentNavigationElement);
        }
        map.put("extraParams", extraParams);
    }

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}