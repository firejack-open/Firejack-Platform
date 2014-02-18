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

package net.firejack.platform.core.utils;

import org.apache.log4j.Logger;

import java.util.*;


public class ConfigContainer {

	protected static Logger logger = Logger.getLogger(ConfigContainer.class);
	private static String host;
	private static int port;

	private static Map<String, String> configs = new HashMap<String, String>();
	private static Map<String, String> opfp = new HashMap<String, String>();
	private static List<ConfigContextListener> listeners = new ArrayList<ConfigContextListener>();

	/**
	 * @param map
	 */
	public static void merge(Properties map) {
		map.putAll(configs);
		configs.putAll((Map) map);
	}

	/**
	 * @param m
	 */
	public static void putAll(Map<String, String> m) {
		if (m != null && !m.isEmpty()) {
			for (Map.Entry<String, String> entry : m.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * @param key
	 * @param value
	 */
	public static void put(String key, String value) {
		if (key.startsWith("opfp.")) {
			opfp.put(key.replaceFirst("opfp.", ""), value);
		} else {
			configs.put(key, value);
			if (value != null) {
				for (ConfigContextListener listener : listeners) {
					listener.put(key, value);
				}
			}
		}
	}

	/**
	 * @param key
	 * @return
	 */
	public static boolean contains(String key) {
		return configs.containsKey(key);
	}

	/**
	 * @param listener
	 */
	public static void addListener(ConfigContextListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return configs.get(key);
	}

	/**
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static String get(String key, String defaultVal) {
		String val = configs.get(key);
		return val != null ? val : defaultVal;
	}

	/**
	 * @return
	 */
	public static Properties getProperties() {
		Properties properties = new Properties();
		properties.putAll(configs);
		return properties;
	}

	/**
	 * @return
	 */
	public static Map<String, String> getOpfp() {
		return opfp;
	}

	/**
	 * @return
	 */
	public static boolean isAppInstalled() {
		return Env.getDefaultEnvFile().exists();
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		ConfigContainer.host = host;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		ConfigContainer.port = port;
	}
}
