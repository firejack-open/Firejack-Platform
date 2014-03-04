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

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;


public final class MiscUtils {

    private static final Logger logger = Logger.getLogger(MiscUtils.class);

    /**
     * @param properties
     * @param checkFileExists
     * @param property
     * @param value
     * @throws java.io.IOException
     */
    public static void setProperties(
            File properties, boolean checkFileExists,
            String property, String value) throws IOException {
        if (checkFileExists && !properties.exists()) {
            logger.error("Properties file [" + properties.getAbsolutePath() + "] does not exist.");
            throw new FileNotFoundException("Properties file does not found.");
//            IOHelper.delete(dbProperties);
        }
        Properties props = new Properties();
        if (properties.exists()) {
            FileReader reader = new FileReader(properties);
            props.load(reader);
            reader.close();
        }
        props.put(property, value);
        FileWriter writer = new FileWriter(properties);
        props.store(writer, null);
        writer.flush();
        writer.close();
    }

	/**
	 * @param properties
	 * @param append
	 * @throws java.io.IOException
	 */
	public static void setProperties(File properties, Map<String, String> append) throws IOException {
		Properties props = new Properties();
		if (properties.exists()) {
			FileReader reader = new FileReader(properties);
			props.load(reader);
			reader.close();
		} else {
			FileUtils.forceMkdir(properties.getParentFile());
		}

		if (properties.exists() || properties.createNewFile()) {
			props.putAll(append);
			FileWriter writer = new FileWriter(properties);
			props.store(writer, null);
			writer.flush();
			writer.close();
		}
	}

    /**
     * @param properties
     * @return
     */
    public static Map<String, String> getProperties(File properties) {
        if (properties == null || !properties.exists()) {
            return Collections.emptyMap();
        }
        Properties props = new Properties();
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(properties);
            props.load(stream);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        Map<String, String> propertiesMap = new HashMap<String, String>();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            propertiesMap.put((String) entry.getKey(), (String) entry.getValue());
        }

        return propertiesMap;
    }

    public static <T> boolean objEquals(T obj1, T obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }

    public static <T> boolean elementEquals(List<T> list1, List<T> list2) {
        return (list2 == null && list1 == null) || (list2 != null && list1 != null &&
                list2.containsAll(list1) && list1.containsAll(list2));
    }

}