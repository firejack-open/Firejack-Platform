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


import net.firejack.platform.core.model.registry.Entry;
import net.firejack.platform.core.model.registry.Environments;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * User: sergey
 * Date: 12:37 PM 11/5/11
 */
public class InstallUtils {
	protected static Logger logger = Logger.getLogger(InstallUtils.class);

	public static final String PROPERTY_XML_FILE = "environment.xml";
	public static final String KEYSTORE_FILE = "store.jks";
	public static final String PROPERTY_FILE = "environment.properties";

	/**
	 * @param file
	 * @return
	 */
	public static <E extends Entry> Environments<E> deserialize(File file) {
		try {
			return deserialize(new BufferedInputStream(new FileInputStream(file)));
		} catch (Exception e) {
			logger.warn("The environment.xml has not been found.");
		}
		return null;
	}

	public static <E extends Entry> Environments<E> deserialize(InputStream stream) {
		return deserialize(stream, Environments.class, Entry.class);
	}

	public static <E extends Entry> Environments<E> deserialize(InputStream stream, Class... classes) {
		try {
			return FileUtils.readJAXB(stream, classes);
		} catch (Exception e) {
			logger.warn("The environment.xml has not been parsed.");
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return null;
	}

	/**
	 * @return
	 */
	public static File getXmlEnv() {
		return Env.getEnvFile(PROPERTY_XML_FILE);
	}

	/**
	 * @return
	 */
	public static File getPropEnv() {
		return Env.getEnvFile(PROPERTY_FILE);
	}

	public static File getKeyStore() {
		return Env.getEnvFile(KEYSTORE_FILE);
	}
}
