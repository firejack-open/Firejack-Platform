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

package net.firejack.platform.model.config.hibernate;


import net.firejack.platform.core.model.registry.DatabaseName;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.dialect.Dialect;

public class OpenFlameNamingStrategy extends ImprovedNamingStrategy {
	private static final long serialVersionUID = 3786236774332079335L;

	private Dialect dialect;
	private boolean upper;

	public OpenFlameNamingStrategy(DatabaseName name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		upper = name == DatabaseName.Oracle;

		Class<?> dl = Class.forName(name.getDialect());
		this.dialect = (Dialect) dl.newInstance();
	}

	public String tableName(String tableName) {
		String name = super.tableName(tableName);

		if (upper) name = name.toUpperCase();
		return quote(name);
	}

	/**
	 * Convert mixed case to underscores
	 */
	public String columnName(String columnName) {
		String name = super.columnName(columnName);

		if (upper) name = name.toUpperCase();
		return quote(name);
	}

	public final String quote(String name) {
		if (name == null) {
			return null;
		}

		if (name.charAt(0) == '`') {
			return dialect.openQuote() + name.substring(1, name.length() - 1) + dialect.closeQuote();
		} else {
			return dialect.openQuote() + name + dialect.closeQuote();
		}
	}
}
