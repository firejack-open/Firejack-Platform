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

package net.firejack.platform.core.model.registry;

import org.apache.commons.lang.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

public enum DatabaseName {

	MySQL("com.mysql.jdbc.Driver",
			"org.hibernate.dialect.MySQL5InnoDBDialect",
			"com.mysql.jdbc.jdbc2.optional.MysqlXADataSource",
			"SELECT 1",
			"${protocol}:mysql://${domain}:${port}",
			"${protocol}:mysql://${domain}:${port}/${schema}"
	),
	Oracle("oracle.jdbc.driver.OracleDriver",
			"org.hibernate.dialect.Oracle10gDialect",
			"oracle.jdbc.xa.client.OracleXADataSource",
			"SELECT 0 FROM DUAL",
			"${protocol}:oracle:thin:@${domain}:${port}:${sid}",
			"${protocol}:oracle:thin:@${domain}:${port}:${sid}"
	),
	MSSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver",
			"org.hibernate.dialect.SQLServer2008Dialect",
			"com.microsoft.sqlserver.jdbc.SQLServerXADataSource",
			"SELECT 1",
			"${protocol}:sqlserver://${domain}:${port};databaseName=${schema}",
			"${protocol}:sqlserver://${domain}:${port};databaseName=${schema}"
	);

	private String driver;
	private String dialect;
	private String xads;
	private String validate;
	private String dbUrlTemplate;
	private String dbSchemaUrlTemplate;

	private DatabaseName(String driver, String dialect, String xads, String validate, String dbUrlTemplate, String dbSchemaUrlTemplate) {
		this.driver = driver;
		this.dialect = dialect;
		this.xads = xads;
		this.validate = validate;
		this.dbUrlTemplate = dbUrlTemplate;
		this.dbSchemaUrlTemplate = dbSchemaUrlTemplate;
	}

	public String getDriver() {
		return driver;
	}

	public String getDialect() {
		return dialect;
	}

	public String getXads() {
		return xads;
	}

	public String getValidate() {
		return validate;
	}

	public String getDbUrlConnection(DatabaseProtocol protocol, String domain, String port) {
		Map<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("protocol", protocol.name().toLowerCase());
		valuesMap.put("domain", domain);
		valuesMap.put("port", port);
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		return sub.replace(dbUrlTemplate);
	}

	public String getDbSchemaUrlConnection(DatabaseProtocol protocol, String domain, String port, String schema) {
		return getDbSchemaUrlConnection(protocol, domain, port, null, schema);
	}

	public String getDbSchemaUrlConnection(DatabaseProtocol protocol, String domain, String port, String sid, String schema) {
		Map<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("protocol", protocol.name().toLowerCase());
		valuesMap.put("domain", domain);
		valuesMap.put("port", port);
		valuesMap.put("sid", sid);
		valuesMap.put("schema", schema);
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		return sub.replace(dbSchemaUrlTemplate);
	}

	/**
	 * @param name
	 * @return
	 */
	public static DatabaseName findByName(String name) {
		DatabaseName value = null;
		for (DatabaseName e : values()) {
			if (e.name().equals(name)) {
				value = e;
				break;
			}
		}
		return value;
	}

}
