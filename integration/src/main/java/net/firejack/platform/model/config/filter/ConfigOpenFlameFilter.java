package net.firejack.platform.model.config.filter;
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


import net.firejack.platform.model.config.GatewayListener;
import net.firejack.platform.model.config.GatewayLoader;
import net.firejack.platform.web.security.filter.OpenFlameFilter;
import org.apache.log4j.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public class ConfigOpenFlameFilter extends OpenFlameFilter implements GatewayListener {
    private static final Logger logger = Logger.getLogger(ConfigOpenFlameFilter.class);
	private FilterConfig filterConfig;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		GatewayLoader.getInstance().addListener(this);
	}

	@Override
	public void start() {
		try {
			super.init(filterConfig);
		} catch (ServletException e) {
			logger.error(e);
		}
	}
}
