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

package net.firejack.platform.core.utils.type;

import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public abstract class BaseUserType<T extends Serializable> implements UserType {
	public static final String ENTRY_DELIMITER = "|";
	public static final String ENTRY_DELIMITER_REGEXP = "\\"+ENTRY_DELIMITER;

	protected List<T> convert(String source) {
		List<T> indexes = new ArrayList<T>();
		for (String token : source.split(ENTRY_DELIMITER_REGEXP)) {
			if (!"".equals(token)) {
				T id = valueOf(token);
				indexes.add(id);
			}
		}
		return indexes;
	}

	protected String convert(List<T> entries) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < entries.size(); i++) {
			builder.append(valueOf0(entries.get(i)));
			if (i != entries.size() - 1) {
				builder.append(ENTRY_DELIMITER);
			}
		}

		return builder.toString();
	}

	protected abstract T valueOf(String element);
	protected abstract String valueOf0(T element);
}