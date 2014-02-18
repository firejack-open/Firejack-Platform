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
