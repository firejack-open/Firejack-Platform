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
