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

import java.util.HashMap;
import java.util.Map;

public class LookupReference extends Tree<LookupReference> {
	private Class clazz;
	private String table;
	private String column;
	private String suffix;
	private Map<String, String> discriminators;

	public LookupReference(Class clazz, String table, String column, String suffix) {
		this.clazz = clazz;
		this.table = table;
		this.column = column;
		this.suffix = suffix;
	}

	public Class getClazz() {
		return clazz;
	}

	public String getTable() {
		return table;
	}

	public String getColumn() {
		return column;
	}

	public String getSuffix() {
		return suffix;
	}

	public Map<String, String> getDiscriminators() {
		return discriminators;
	}

	public void addDiscriminator(String type, String suffix) {
		if (discriminators == null) {
			discriminators = new HashMap<String, String>();
		}
		discriminators.put(type, suffix);
	}

	public boolean isDiscriminatorTable() {
		return column != null;
	}
}
