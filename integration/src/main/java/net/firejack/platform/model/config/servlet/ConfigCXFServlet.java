package net.firejack.platform.model.config.servlet;

import net.firejack.platform.model.config.GatewayListener;
import net.firejack.platform.model.config.GatewayLoader;
import org.apache.cxf.transport.servlet.CXFServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

public class ConfigCXFServlet extends CXFServlet implements GatewayListener {
	private static final long serialVersionUID = -2351271954946703301L;
	private transient ServletConfig config;


	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		GatewayLoader.getInstance().addListener(this);
	}

	@Override
	public void start() {
		try {
			super.init(config);
		} catch (ServletException e) {
			log(e.getMessage(), e);
		}
	}
}
