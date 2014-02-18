/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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
