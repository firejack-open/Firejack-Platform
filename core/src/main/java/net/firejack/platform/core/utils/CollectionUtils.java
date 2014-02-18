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

import org.apache.commons.collections.map.MultiValueMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {

	/**
	 * @param collection
	 *
	 * @return
	 */
	public static boolean isEmpty(Collection collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * @param map
	 *
	 * @return
	 */
	public static boolean isEmpty(Map map) {
		return map == null || map.isEmpty();
	}

	/**
	 *
	 * @param objects
	 * @param key
	 * @param value
	 * @return
	 */
	public static MultiValueMap create(List<Object[]> objects, int key, int value) {
		MultiValueMap map = new MultiValueMap();

		if (objects != null) {
			for (Object[] object : objects) {
				map.put(object[key], object[value]);
			}
		}
		return map;
	}
}