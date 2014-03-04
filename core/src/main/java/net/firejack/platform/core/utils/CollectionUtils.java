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