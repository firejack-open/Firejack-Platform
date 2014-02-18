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