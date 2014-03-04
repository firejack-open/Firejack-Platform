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

package net.firejack.platform.utils;

import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.utils.ConfigContainer;
import net.firejack.platform.core.utils.Env;
import net.firejack.platform.core.utils.InstallUtils;
import net.firejack.platform.web.security.x509.KeyUtils;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum OpenFlameConfig {

	DOMAIN_URL("system.host"),
	PORT("system.port"),
	CONTEXT_URL("context.url"),

	DB_PORT("db.port", true),
	DB_URL("db.url"),
	DB_SCHEMA("db.schema", true),
	DB_HOST("db.host", true),
	DB_USER_NAME("db.username"),
	DB_USER_PASSWORD("db.password"),

	MC_SERVER_URL("memcached.url"),
	MC_PORT("memcached.port"),

	APP_ADMIN_NAME("app.admin.name", false),
	APP_ADMIN_EMAIL("app.admin.email", false),
	APP_ADMIN_PASSWORD("app.admin.password", false),

	MASTER_URL("opf.master.url", false),

	APP_ENV_PROPERTIES("app.env.properties", ""),
	APP_ENV_XML("app.env.xml", ""),

	SESSION_EXPIRATION_PERIOD("session.expiration.period", "43200000");    //12 h
//	SESSION_EXPIRATION_PERIOD("session.expiration.period", "300000");    //5 min

	private boolean saved;
	private String key;
	private String defaultVal;

	OpenFlameConfig(String key) {
		this.key = key;
		this.saved = true;
	}

	OpenFlameConfig(String key, boolean saved) {
		this.key = key;
		this.saved = saved;
	}

	OpenFlameConfig(String key, String defaultVal) {
		this(key);
		this.defaultVal = defaultVal;
	}

	public boolean isSaved() {
		return saved;
	}

	/**
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return ConfigContainer.get(key, defaultVal);
	}

	/**
	 * @param value
	 */
	public void setValue(String value) {
		ConfigContainer.put(key, value);
	}

	/**
	 * @return
	 */
	public boolean exist() {
		return ConfigContainer.contains(key);
	}

	/**
	 * @return
	 */
	public static Map<String, String> filter() {
		Map<String, String> map = new HashMap<String, String>();
		for (OpenFlameConfig config : values()) {
			if (config.isSaved() && config.exist() && config.getValue() != null) {
				map.put(config.getKey(), config.getValue());
			}
		}
		return map;
	}

	public static void save() {
		try {
			KeyUtils.setProperties(Env.getDefaultEnvFile(), InstallUtils.getKeyStore(), filter());
		} catch (IOException e) {
			throw new BusinessFunctionException(MessageKeys.APP_FAILED_TO_SAVE_SETTINGS, null, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
}
