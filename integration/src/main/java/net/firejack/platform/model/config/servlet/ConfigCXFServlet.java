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

package net.firejack.platform.model.config.servlet;

import net.firejack.platform.model.config.GatewayListener;
import net.firejack.platform.model.config.GatewayLoader;
import org.apache.cxf.transport.servlet.CXFServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

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
