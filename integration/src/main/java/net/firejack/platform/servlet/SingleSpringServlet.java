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
