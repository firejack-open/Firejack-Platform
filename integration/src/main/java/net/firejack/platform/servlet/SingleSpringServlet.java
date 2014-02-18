/**
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
package net.firejack.platform.servlet;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.xml.bind.v2.ClassFactory;
import net.firejack.platform.core.domain.AbstractDTO;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.ws.rs.core.Context;
import java.util.Map;

public class SingleSpringServlet extends SpringServlet {
	private static final long serialVersionUID = -4929288909546306030L;
	private ConfigurableApplicationContext context;

	@Override
	protected void init(WebConfig config) throws ServletException {
		try {
			this.context = (ConfigurableApplicationContext) WebApplicationContextUtils.getWebApplicationContext(config.getServletContext(), WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
			super.init(config);
		} finally {
			ClassFactory.cleanCache();
		}
	}

	@Override
	protected void configure(WebConfig wc, ResourceConfig rc, WebApplication wa) {
		super.configure(wc, rc, wa);
		Map<String, AbstractDTO> map = context.getBeansOfType(AbstractDTO.class);
		Class[] classes = new Class[map.size()];
		int i = 0;
		for (AbstractDTO dto : map.values()) {
			classes[i++] = dto.getClass();
		}
		rc.getSingletons().add(new SingletonTypeInjectableProvider<Context, Class[]>(Class[].class, classes) {});
	}

	@Override
	protected ConfigurableApplicationContext getContext() {
		return context;
	}

	@Override
	public void destroy() {
		super.destroy();
		this.context = null;
	}
}
