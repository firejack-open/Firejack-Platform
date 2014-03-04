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
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.model.registry.domain.Environment;
import net.firejack.platform.core.model.registry.domain.PackageModel;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * User: sergey
 * Date: 12:37 PM 11/5/11
 */
public abstract class EnvironmentsUtils extends InstallUtils {
	private static final Logger logger = Logger.getLogger(EnvironmentsUtils.class);

	/**
	 * @param file
	 * @return
	 */
	public static Environments<Environment> deserialize(File file) {
		try {
			return deserialize(new BufferedInputStream(new FileInputStream(file)));
		} catch (Exception e) {
			logger.warn("The environment.xml has not been found.");
		}
		return null;
	}

	public static Environments<Environment> deserialize(InputStream stream) {
		return deserialize(stream, Environments.class, Environment.class);
	}

	/**
	 * @param file
	 * @return
	 */
	public static Map<String, String> convertFromXml(File file) {
		Environments<Environment> environments = deserialize(file);
		if (environments != null && environments.getEnvironments() != null && !environments.getEnvironments().isEmpty()) {
			Environment opfEnvironment = findOpfEnvironment(environments);
			if (opfEnvironment != null) {
				return remap(opfEnvironment);
			}
		} else {
			logger.warn("Environments file is empty.");
		}
		return Collections.emptyMap();
	}

	public static Map<String, String> convertFromXml(InputStream stream) {
		Environments<Environment> environments = deserialize(stream);
		if (environments != null && environments.getEnvironments() != null && !environments.getEnvironments().isEmpty()) {
			Environment opfEnvironment = findOpfEnvironment(environments);
			if (opfEnvironment != null) {
				return remap(opfEnvironment);
			}
		} else {
			logger.warn("Environments file is empty.");
		}
		return Collections.emptyMap();
	}

	private static Environment findOpfEnvironment(Environments<Environment> environments) {
		for (Environment item : environments.getEnvironments()) {
			if (item.getDatabases() != null && findOpfLocal(item.getDatabases()) != null) {
				return item;
			} else if (item.getFilestores() != null && findOpfLocal(item.getFilestores()) != null) {
				return item;
			} else if (item.getServers() != null && findOpfLocal(item.getServers()) != null) {
				return item;
			} else if (item.getSystem() != null && item.getSystem().getPath().contains(OpenFlame.SYSTEM)) {
				return item;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static LookupModel findOpfLocal(Collection collection) {
		if (collection.isEmpty()) return null;
		for (LookupModel model : (Collection<LookupModel>) collection) {
			if (model.getPath() != null && model.getPath().contains(OpenFlame.SYSTEM)) {
				return model;
			}
		}
		return null;
	}

	public static List<Entry> convert(Environment environment, PackageModel model) {
		List<Entry> entries = new ArrayList<Entry>();
		Map<String, String> result = new HashMap<String, String>();
		result.putAll(ClassUtils.transformPlaceHolderEntity(environment.getDatabases(), false));
		result.putAll(ClassUtils.transformPlaceHolderEntity(environment.getFilestores(), false));
		result.putAll(ClassUtils.transformPlaceHolderEntity(environment.getSystem()));

		if (model != null) {
			Map<String, String> map = ClassUtils.transformPlaceHolderEntity(model, false);
			result.putAll(map);
		}

		for (Map.Entry<String, String> entry : result.entrySet()) {
			entries.add(new Entry(entry.getKey(), entry.getValue()));
		}
		return entries;
	}

	private static Map<String, String> remap(Environment env) {
		Map<String, String> result = new HashMap<String, String>();
		result.putAll(ClassUtils.transformPlaceHolderEntity(env.getDatabases()));
		result.putAll(ClassUtils.transformPlaceHolderEntity(env.getFilestores(), false));
		result.putAll(ClassUtils.transformPlaceHolderEntity(env.getSystem()));
		return result;
	}
}
