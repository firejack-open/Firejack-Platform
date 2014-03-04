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

import net.firejack.platform.core.model.registry.DatabaseName;
import net.firejack.platform.core.model.registry.DatabaseProtocol;
import net.firejack.platform.core.utils.db.DBUtils;
import net.firejack.platform.utils.OpenFlameConfig;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Enumeration;


public class OpenFlameDataSource extends BasicDataSource {

    protected static Logger logger = Logger.getLogger(OpenFlameDataSource.class);

	private DatabaseName name;
    private String sid;
    private String schema;

    /***/
    public OpenFlameDataSource() {
        if (ConfigContainer.isAppInstalled()) {
            refreshDBProperties();
        }
    }

	public DatabaseName getName() {
		return name;
	}

	public void setName(DatabaseName name) {
		this.name = name;
	}

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    /***/
    public void refreshDBProperties() {
        url = OpenFlameConfig.DB_URL.getValue();
        password = OpenFlameConfig.DB_USER_PASSWORD.getValue();
        username = OpenFlameConfig.DB_USER_NAME.getValue();
    }

	public DataSource database() {
		String user = OpenFlameConfig.DB_USER_NAME.getValue();
		String password = OpenFlameConfig.DB_USER_PASSWORD.getValue();
		String host = OpenFlameConfig.DB_HOST.getValue();
		String port = OpenFlameConfig.DB_PORT.getValue();

		return convert(host, port, null, user, password);
	}

	public DataSource schema() {
		String user = OpenFlameConfig.DB_USER_NAME.getValue();
		String password = OpenFlameConfig.DB_USER_PASSWORD.getValue();
		String host = OpenFlameConfig.DB_HOST.getValue();
		String port = OpenFlameConfig.DB_PORT.getValue();
		String schema = OpenFlameConfig.DB_SCHEMA.getValue();

		return convert(host, port, schema, user, password);
	}

	/**
	 * @param host
	 * @param port
	 * @param schema
	 * @param user
	 * @param password
	 * @return
	 */
	public DataSource convert(String host, String port, String schema, String user, String password) {
		if (StringUtils.isBlank(host) || StringUtils.isBlank(port)) {
			return null;
		}
		String dbUrl;
		if (StringUtils.isNotBlank(schema)) {
			dbUrl = DatabaseName.MySQL.getDbSchemaUrlConnection(DatabaseProtocol.JDBC, host, port, schema);
		} else {
			dbUrl = DatabaseName.MySQL.getDbUrlConnection(DatabaseProtocol.JDBC, host, port);
		}
		return DBUtils.populateDataSource(getDriverClassName(), dbUrl, user, password);
	}

	public static boolean ping(DatabaseName type, String host, String port, String user, String password, String sid, String schema) {
		try {
			Class.forName(type.getDriver());
			String url;
			if (type == DatabaseName.MySQL) {
				url = type.getDbUrlConnection(DatabaseProtocol.JDBC, host, port);
			} else {
				url = type.getDbSchemaUrlConnection(DatabaseProtocol.JDBC, host, port, sid, schema);
			}

			Connection connection = DriverManager.getConnection(url, user, password);
			if (connection == null) {
				return false;
			}

			Statement statement = connection.createStatement();
			statement.execute(type.getValidate());
			connection.close();
			return true;
		} catch (Exception e) {
			logger.error("Ping to db has failed.");
			return false;
		}
	}

	/**
     * Creates a JDBC connection factory for this datasource.  This method only
     * exists so subclasses can replace the implementation class.
     */
    protected ConnectionFactory createConnectionFactory() throws SQLException {
        try {
            return super.createConnectionFactory();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public synchronized void close() throws SQLException {
        super.close();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            DriverManager.deregisterDriver(drivers.nextElement());
        }
    }
}
